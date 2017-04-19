/*
 *  streams library
 *
 *  Copyright (C) 2011-2014 by Christian Bockermann, Hendrik Blom
 * 
 *  streams is a library, API and runtime environment for processing high
 *  volume data streams. It is composed of three submodules "stream-api",
 *  "stream-core" and "stream-runtime".
 *
 *  The streams library (and its submodules) is free software: you can 
 *  redistribute it and/or modify it under the terms of the 
 *  GNU Affero General Public License as published by the Free Software 
 *  Foundation, either version 3 of the License, or (at your option) any 
 *  later version.
 *
 *  The stream.ai library (and its submodules) is distributed in the hope
 *  that it will be useful, but WITHOUT ANY WARRANTY; without even the implied 
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/.
 */
package stream.runtime;

import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import stream.Data;
import stream.Process;
import stream.ProcessContext;
import stream.container.IContainer;
import stream.data.DataFactory;
import stream.io.Queue;
import stream.io.Sink;
import stream.io.Source;
import stream.runtime.rpc.RMINamingService;
import stream.runtime.setup.ObjectCreator;
import stream.runtime.setup.factory.ObjectFactory;
import stream.runtime.setup.factory.ProcessorFactory;
import stream.runtime.setup.handler.ContainerRefElementHandler;
import stream.runtime.setup.handler.DocumentHandler;
import stream.runtime.setup.handler.LibrariesElementHandler;
import stream.runtime.setup.handler.MonitorElementHandler;
import stream.runtime.setup.handler.ProcessElementHandler;
import stream.runtime.setup.handler.PropertiesHandler;
import stream.runtime.setup.handler.QueueElementHandler;
import stream.runtime.setup.handler.ServiceElementHandler;
import stream.runtime.setup.handler.SinkElementHandler;
import stream.runtime.setup.handler.StreamElementHandler;
import stream.runtime.setup.handler.SystemPropertiesHandler;
import stream.service.NamingService;
import stream.service.Service;
import stream.util.Variables;
import stream.util.XIncluder;
import stream.util.XMLUtils;
import stream.utils.PrintGraph;
import streams.application.ComputeGraph;
import streams.runtime.Hook;
import streams.runtime.Signals;
import streams.runtime.Supervisor;

/**
 * A process-container is a collection of processes that run independently. Each
 * process is a self-contained thread that is reading from a data-stream (or
 * queue).
 * 
 * The process-container is responsible for instantiating the processor
 * elements, grouping them into processes (threads) and creating the
 * data-streams defined in the corresponding XML configuration.
 * 
 * Upon startup, the process-container will start all processes (threads) and
 * wait until all of them have finished.
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public class ProcessContainer implements IContainer, Runnable {

	static Logger log = LoggerFactory.getLogger(ProcessContainer.class);

	final static List<ProcessContainer> container = new ArrayList<ProcessContainer>();

	static {
		// The rescue-shutdown handler in case the VM was killed by a signal...
		//
		log.debug("Adding container shutdown-hook");
		Thread t = new Thread() {
			public void run() {
				log.debug("Executing shutdown-hook...");
				if ("disabled".equalsIgnoreCase(System.getProperty("container.shutdown-hook"))) {
					log.warn("Shutdown-hook disabled...");
					return;
				}

				log.debug("Running shutdown-hook...");
				for (ProcessContainer pc : container) {
					log.debug("Sending shutdown signal to {}", pc);
					pc.shutdown();
				}
			}
		};

		t.setDaemon(true);
		java.lang.Runtime.getRuntime().addShutdownHook(t);
	}

	protected final ObjectFactory objectFactory = ObjectFactory.newInstance();
	protected final ProcessorFactory processorFactory = new ProcessorFactory(objectFactory);

	/** The final compute graph that will be build... */
	protected final ComputeGraph depGraph = new ComputeGraph();

	/**
	 * The dependency-injection management, required for gathering dependencies
	 * while the compute graph is being built up.
	 */
	protected final DependencyInjection dependencyInjection = new DependencyInjection();

	/**
	 * The name of this container, used in lookup URIs (e.g.
	 * //container-name/serice-name )
	 */
	protected String name = null;

	/** The global container context */
	protected final ContainerContext context;

	/** The set of data streams (sources) */
	protected final Map<String, Source> streams = new LinkedHashMap<String, Source>();

	/** The set of sinks */
	protected final Map<String, Sink> sinks = new LinkedHashMap<String, Sink>();

	/**
	 * The list of data-stream-queues, that can be fed from external instances
	 */
	protected final Map<String, Queue> listeners = new LinkedHashMap<String, Queue>();

	/** The list of processes running in this container */
	protected final List<Process> processes = new ArrayList<Process>();

	protected final Map<Process, ProcessContext> processContexts = new LinkedHashMap<Process, ProcessContext>();

	protected final List<ProcessThread> worker = new ArrayList<ProcessThread>();

	protected final List<ServiceReference> serviceRefs = new ArrayList<ServiceReference>();

	protected final Map<String, ElementHandler> elementHandler = new HashMap<String, ElementHandler>();

	protected final List<DocumentHandler> documentHandler = new ArrayList<DocumentHandler>();

	protected NamingService namingService = null;

	protected final List<LifeCycle> lifeCyleObjects = new ArrayList<LifeCycle>();

	boolean server = true;

	protected long runtime;

	protected Long startTime = 0L;
	protected Variables containerVariables = new Variables();
	private Exception failFastReason = null;

	final static String[] extensions = new String[] { "stream.moa.MoaObjectFactory",
			"stream.script.JavaScriptProcessorFactory" };

	final static Map<String, ElementHandler> autoHandlers = new LinkedHashMap<String, ElementHandler>();

	Supervisor supervisor;

	static {

		for (String ext : extensions) {
			try {
				Class<?> clazz = Class.forName(ext);
				ObjectCreator creator = (ObjectCreator) clazz.newInstance();
				ObjectFactory.registerObjectCreator(creator);
				log.debug("Registered extension {}", ext);
			} catch (Exception e) {
				log.debug("Failed to register extension '{}': {}", ext, e.getMessage());
				if (log.isTraceEnabled())
					e.printStackTrace();
			}
		}

		String[] handlerClasses = new String[] { "streams.esper.EsperEngineElementHandler" };

		for (String handlerClass : handlerClasses) {
			try {
				Class<?> clazz = Class.forName(handlerClass);
				ElementHandler handler = (ElementHandler) clazz.newInstance();
				String key = handler.getKey();
				autoHandlers.put(key, handler);

			} catch (Exception e) {
				log.debug("Failed to register handler {}", handlerClass);
				if (log.isTraceEnabled())
					e.printStackTrace();
			}
		}
	}

	public static Document parseDocument(URL url) throws Exception {

		// Get Document
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(url.openStream());

		return doc;
	}

	/**
	 * This constructor creates a new process-container instance by parsing an
	 * XML document located at the specified URL.
	 * 
	 * @param url
	 * @throws Exception
	 */
	public ProcessContainer(URL url) throws Exception {
		this(url, null);
	}

	public ProcessContainer(URL url, Map<String, ElementHandler> customElementHandler) throws Exception {
		this(parseDocument(url), customElementHandler, null);
	}

	public ProcessContainer(URL url, Map<String, ElementHandler> customElementHandler, Map<String, String> vars)
			throws Exception {
		this(parseDocument(url), customElementHandler, vars);
	}

	public ProcessContainer(Document doc, Map<String, ElementHandler> customElementHandler,
			Map<String, String> variables) throws Exception {

		if (variables != null)
			containerVariables.addVariables(variables);
		container.add(this);

		LibrariesElementHandler libHandler = new LibrariesElementHandler(objectFactory);

		// DocumentHandler ORDER IS VERY IMPORTANT!!!
		// "Default" Parameter first. Property-Files from System-properties.
		documentHandler.add(new PropertiesHandler());
		// Add System-properties
		documentHandler.add(new SystemPropertiesHandler());
		documentHandler.add(libHandler);

		// auto-discovered handler
		for (String elem : autoHandlers.keySet()) {
			log.debug("Adding auto-discovered handler for element '{}': {}", elem, autoHandlers.get(elem));
			elementHandler.put(elem, autoHandlers.get(elem));
		}

		// ElementHandler
		// Container-Ref
		elementHandler.put("Container-Ref", new ContainerRefElementHandler(objectFactory));

		// IO
		elementHandler.put("Stream", new StreamElementHandler(objectFactory));

		elementHandler.put("Sink", new SinkElementHandler());
		elementHandler.put("Queue", new QueueElementHandler());

		elementHandler.put("Monitor", new MonitorElementHandler(objectFactory, processorFactory));
		elementHandler.put("Process", new ProcessElementHandler(objectFactory, processorFactory));

		elementHandler.put("Service", new ServiceElementHandler(objectFactory));
		elementHandler.put("Libs", libHandler);

		if (customElementHandler != null)
			elementHandler.putAll(customElementHandler);

		XIncluder includer = new XIncluder();

		Variables v = new Variables(containerVariables);

		doc = includer.perform(doc, v);

		log.debug(XMLUtils.toString(doc));

		log.debug("XML created and preprocessed.");

		// Container
		Element root = doc.getDocumentElement();
		Map<String, String> attr = objectFactory.getAttributes(root);

		String applicationId = null;
		if (root.hasAttribute("id")) {
			applicationId = root.getAttribute("id");
			name = applicationId;
		} else {
			applicationId = UUID.randomUUID().toString();
		}

		if (System.getProperty("container.address") != null) {
			attr.put("address", System.getProperty("container.address"));
		}
		if (System.getProperty("container.port") != null) {
			attr.put("port", System.getProperty("container.port"));
		}

		try {
			server = new Boolean(attr.get("server"));
		} catch (Exception e) {
			server = true;
		}

		if (!root.getNodeName().equalsIgnoreCase("application") && !root.getNodeName().equalsIgnoreCase("container"))
			throw new Exception("Expecting root element to be 'container'!");

		// Container Adress
		String host = "localhost";

		if (System.getProperty("hostname") != null) {
			host = System.getProperty("hostname");
		} else {
			try {
				host = InetAddress.getLocalHost().getHostAddress(); // .getHostName();
				name = InetAddress.getLocalHost().getHostName();
				if (name.indexOf(".") > 0) {
					name = name.substring(0, name.indexOf("."));
				}
			} catch (Exception e) {
				name = UUID.randomUUID().toString();
			}

			log.debug("Default hostname is: {}", host);
			// String host = "localhost";
			if (attr.containsKey("address") && !attr.get("address").trim().isEmpty()) {
				host = InetAddress.getByName(attr.get("address")).getHostAddress();
				log.debug("Container address will be {}", host);
			}
		}

		Integer port = 0;
		if (attr.containsKey("port") && !attr.get("port").trim().isEmpty()) {
			port = new Integer(attr.get("port"));
			log.debug("Container port will be {}", port);
		}
		if (root.hasAttribute("id"))
			name = root.getAttribute("id");

		try {
			String nsClass = root.getAttribute("namingService");
			if (nsClass != null && !nsClass.trim().isEmpty())
				namingService = (NamingService) objectFactory.create(nsClass, attr,
						ObjectFactory.createConfigDocument(root));
		} catch (Exception e) {
			log.error("Faild to instantiate naming service '{}': {}", root.getAttribute("namingService"),
					e.getMessage());
			throw new Exception("Faild to instantiate naming service '" + root.getAttribute("namingService") + "': "
					+ e.getMessage());
		}

		// RemoteClassServer classServer = new RemoteClassServer(0);
		// classServer.start();

		//
		// create the default NamingService if none has been specified, yet.
		//
		if (namingService == null) {

			if (attr.containsKey("address")) {
				log.debug("Creating RMI naming-service...");
				System.setProperty("java.rmi.server.hostname", host);
				namingService = new RMINamingService(name, host, port, true);
			} else {
				log.debug(
						"No address specified, using local naming-service. Container will not be able to reference other containers!");
				namingService = new DefaultNamingService();
			}
		}

		if (namingService instanceof LifeCycle) {
			lifeCyleObjects.add((LifeCycle) namingService);
		}

		log.debug("Using naming-service {}", namingService);
		context = new ContainerContext(applicationId + "@" + System.currentTimeMillis(), name, namingService);
		this.init(doc);
	}

	/**
	 * @see stream.runtime.IContainer#computeGraph()
	 */
	@Override
	public ComputeGraph computeGraph() {
		return depGraph;
	}

	/**
	 * @see stream.runtime.IContainer#getStreams()
	 */
	@Override
	public Set<Source> getStreams() {
		return new LinkedHashSet<Source>(this.streams.values());
	}

	/**
	 * @see stream.runtime.IContainer#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	public List<LifeCycle> lifeCycles() {
		return this.lifeCyleObjects;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see stream.runtime.IContainer#getContext()
	 */
	@Override
	public ContainerContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see stream.runtime.IContainer#getProcesses()
	 */
	@Override
	public List<Process> getProcesses() {
		return processes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see stream.runtime.IContainer#getServiceRefs()
	 */
	@Override
	public List<ServiceReference> getServiceRefs() {
		return serviceRefs;
	}

	public void register(LifeCycle lc) {
		if (!this.lifeCyleObjects.contains(lc)) {
			log.debug("Registering new life-cycle object {}", lc);
			this.lifeCyleObjects.add(lc);
		}
	}

	private void init(Document doc) throws Exception {
		Element root = doc.getDocumentElement();

		if (root.getAttribute("import") != null) {
			String[] pkgs = root.getAttribute("import").split(",");
			for (String pkg : pkgs) {
				if (!pkg.trim().isEmpty())
					objectFactory.addPackage(pkg.trim());
			}
		}

		String name = root.getAttribute("name");
		if (name == null)
			name = "local";

		for (DocumentHandler handle : documentHandler) {
			handle.handle(this, doc, containerVariables, dependencyInjection);
		}

		objectFactory.addVariables(context.getProperties());
		objectFactory.addVariables(containerVariables);

		NodeList children = root.getChildNodes();

		if (context.getProperties().get("container.datafactory") != null) {
			log.debug("Using {} as default DataFactory for this container...",
					context.getProperties().get("container.datafactory"));
			Class<?> dataFactoryClass = Class.forName(context.getProperties().get("container.datafactory"));
			DataFactory.setDefaultDataFactory((DataFactory) dataFactoryClass.newInstance());
		}

		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);

			if (node.getNodeType() != Node.ELEMENT_NODE)
				continue;

			Element element = (Element) node;
			for (ElementHandler handler : this.elementHandler.values()) {
				if (handler.handlesElement(element)) {
					handler.handleElement(this, element, containerVariables, dependencyInjection);
					continue;
				}
			}

		}

		// connectProcesses();

		// injectServices();

		try {
			dependencyInjection.injectDependencies(depGraph, namingService);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		// // Special Treatment for properties
		// DocumentHandler ph = new PropertiesHandler();
		// Variables pv = new Variables();
		// ph.handle(this, doc, pv, dependencyInjection);
		// context.getProperties().putAll(pv);

		context.setProperty("xml", XMLUtils.toString(doc));

		drawGraph();

		log.debug("ProcessContainer is initialized and ready to start:{}", this.toString());
	}

	private void drawGraph() {
		ComputeGraph g = computeGraph();

		String str = PrintGraph.print(g);
		if (str != null && !str.trim().isEmpty()) {
			stream.run.log.info("Compute graph:\n\n{}", str);
		}
	}

	public void registerQueue(String id, Queue queue, boolean externalListener) throws Exception {
		log.debug("A new queue '{}' is registered for id '{}'", queue, id);
		if (externalListener) {
			listeners.put(id, queue);
		}
		registerStream(id, queue);
		registerSink(id, queue);
		// context.register(id, queue);
	}

	public void registerSink(String id, Sink sink) {
		sinks.put(id, sink);
	}

	public void registerStream(String id, Source stream) {
		streams.put(id, stream);
	}

	public void run() {
		try {
			runtime = execute();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public long execute() throws Exception {

		if (!container.contains(this)) {
			log.debug("Registering new container {}", this);
			container.add(this);

			Signals.register(new Hook() {
				@Override
				public void signal(int flags) {
					log.info("Handling signal {}", flags);
				}
			});
		}

		startTime = System.currentTimeMillis();
		ContainerController controller = new ContainerController(this);
		log.debug("Registering container-controller {}", controller);
		this.namingService.register(".ctrl", controller);

		// this.injectServices();

		Map<String, Service> services = computeGraph().services();
		for (String name : services.keySet()) {
			log.info("Registering service '{}' => {}", name, services.get(name));
			namingService.register(name, services.get(name));
		}

		if (!server && streams.isEmpty() && listeners.isEmpty())
			throw new Exception("No data-stream defined!");

		log.debug("Need to handle {} sources: {}", streams.size(), streams.keySet());

		log.debug("Experiment contains {} stream processes", processes.size());

		log.debug("Initializing all DataStreams...");
		if (streams.keySet().isEmpty())
			log.debug("No dataStreams to initialize");
		for (String name : streams.keySet()) {
			Source stream = streams.get(name);
			log.debug("Initializing stream '{}'", name);
			stream.init();
		}

		log.debug("Initializing all Sinks...");
		for (String name : sinks.keySet()) {
			Sink sink = sinks.get(name);
			log.debug("Initializing sink '{}'", name);
			sink.init();
		}

		for (LifeCycle lc : this.lifeCyleObjects) {
			log.info("Initializing life-cycle for {}", lc);
			lc.init(context);
		}

		final Supervisor supervisor = new Supervisor(computeGraph()) {
			/**
			 * @see streams.runtime.Supervisor#processError(stream.Process,
			 *      java.lang.Exception)
			 */
			@Override
			public void processError(Process p, Exception e) {
				super.processError(p, e);
				log.error("Process {} signaled an error: {}", p, e.getMessage());
				log.debug("Forcing fail-fast shutdown of application...");
				failFastReason = e;
				shutdown();
			}
		};
		this.supervisor = supervisor;

		log.debug("Creating {} active processes...", processes.size());
		long start = System.currentTimeMillis();
		int processesStarted = 0;
		for (Process spu : processes) {

			ProcessContext ctx = this.processContexts.get(spu);
			if (ctx == null) {
				ctx = new ProcessContextImpl("process:" + spu.hashCode(), context);
				processContexts.put(spu, ctx);
			}

			final ProcessThread worker = new ProcessThread(spu, context);
			worker.addListener(supervisor);

			log.debug("Initializing stream-process [{}]", spu);
			worker.init();

			log.debug("Starting stream-process [{}]", spu);
			worker.start();

			Signals.register(new Hook() {
				@Override
				public void signal(int flags) {
					worker.signal(flags);
				}
			});

			log.debug("Stream-process started.");
			processesStarted++;
		}

		if (failFastReason != null) {
			log.error("Exception occurred: {}", failFastReason.getMessage());
			throw failFastReason;
		}

		// Signals.register(supervisor);

		log.debug("{} processes started...", processesStarted);
		while (supervisor.processesDone() < processesStarted) {
			log.debug("Waiting for processes to finish...");
			supervisor.waitForProcesses();
			log.debug("{} of {} processes finished...", supervisor.processesDone(), processesStarted);
		}

		long end = System.currentTimeMillis();
		log.trace("Running processes: {}", processes);
		log.debug("ProcessContainer finished all processes after {} ms", (end - start));
		return end - start;
	}

	/**
	 * @see stream.runtime.IContainer#getVariables()
	 */
	@Override
	public Variables getVariables() {
		return this.containerVariables;
	}

	public Set<String> getStreamListenerNames() {
		return listeners.keySet();
	}

	public ObjectFactory getObjectFactory() {
		return this.objectFactory;
	}

	public void dataArrived(String key, Data item) {
		if (listeners.containsKey(key)) {
			log.debug("Adding item {} into queue {}", item, key);
			try {
				listeners.get(key).write(item);
			} catch (Exception e) {
				log.error("Failed to inject arriving data item into queue {}: {}", key, e.getMessage());
			}
		} else {
			log.warn("No listener defined for {}", key);
		}
	}

	public void shutdown() {
		log.debug("shutdown()");

		if (this.supervisor != null) {

			log.debug("Calling supervisor.signal(0)!");
			supervisor.signal(0);

			for (ProcessThread wt : this.worker) {
				try {
					log.info("Sending signal to {}", wt);
					wt.signal(0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			log.debug("Waiting for remaining processes...");
			while (supervisor.processesRunning() > 0) {
				try {
					log.debug("    waiting for {} process(es) to finish...", supervisor.processesRunning());
					Thread.sleep(250);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			log.info("No supervisor installed; no clean-up!");
		}
	}

	/**
	 * @param process
	 * @param ctx
	 */
	public void setProcessContext(stream.Process process, ProcessContext ctx) {
		processContexts.put(process, ctx);
	}

	@Override
	public NamingService getNamingService() {
		return namingService;
	}
}