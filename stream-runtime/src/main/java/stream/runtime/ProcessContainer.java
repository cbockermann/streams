/*
 *  streams library
 *
 *  Copyright (C) 2011-2012 by Christian Bockermann, Hendrik Blom
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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import stream.ComputeGraph;
import stream.Data;
import stream.Process;
import stream.ProcessContext;
import stream.data.DataFactory;
import stream.io.BlockingQueue;
import stream.io.Queue;
import stream.io.Source;
import stream.runtime.rpc.RMINamingService;
import stream.runtime.setup.ObjectCreator;
import stream.runtime.setup.ObjectFactory;
import stream.runtime.setup.ProcessorFactory;
import stream.runtime.setup.ServiceReference;
import stream.runtime.setup.handler.ContainerRefElementHandler;
import stream.runtime.setup.handler.DocumentHandler;
import stream.runtime.setup.handler.LibrariesElementHandler;
import stream.runtime.setup.handler.MonitorElementHandler;
import stream.runtime.setup.handler.ProcessElementHandler;
import stream.runtime.setup.handler.PropertiesHandler;
import stream.runtime.setup.handler.QueueElementHandler;
import stream.runtime.setup.handler.ServiceElementHandler;
import stream.runtime.setup.handler.StreamElementHandler;
import stream.runtime.setup.handler.SystemPropertiesHandler;
import stream.runtime.shutdown.LocalShutdownCondition;
import stream.runtime.shutdown.ServerShutdownCondition;
import stream.runtime.shutdown.ShutdownCondition;
import stream.service.NamingService;

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
public class ProcessContainer implements IContainer {

	static Logger log = LoggerFactory.getLogger(ProcessContainer.class);

	final static List<ProcessContainer> container = new ArrayList<ProcessContainer>();

	private static Boolean runShutdownHook = true;

	static {
		// The rescue-shutdown handler in case the VM was killed by a signal...
		//
		log.debug("Adding container shutdown-hook");
		Thread t = new Thread() {
			public void run() {

				if (!runShutdownHook) {
					return;
				}

				if ("disabled".equalsIgnoreCase(System
						.getProperty("container.shutdown-hook"))) {
					log.warn("Shutdown-hook disabled...");
					return;
				}

				log.info("Running shutdown-hook...");
				for (ProcessContainer pc : container) {
					log.info("Sending shutdown signal to {}", pc);
					pc.shutdown();
				}
			}
		};

		t.setDaemon(true);
		java.lang.Runtime.getRuntime().addShutdownHook(t);
	}

	protected final ObjectFactory objectFactory = ObjectFactory.newInstance();
	protected final ProcessorFactory processorFactory = new ProcessorFactory(
			objectFactory);

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

	/** The list of data-stream-queues, that can be fed from external instances */
	protected final Map<String, BlockingQueue> listeners = new LinkedHashMap<String, BlockingQueue>();

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

	protected Long startTime = 0L;
	protected Variables containerVariables = new Variables();

	final static String[] extensions = new String[] {
			"stream.moa.MoaObjectFactory",
			"stream.script.JavaScriptProcessorFactory" };

	static {

		for (String ext : extensions) {
			try {
				Class<?> clazz = Class.forName(ext);
				ObjectCreator creator = (ObjectCreator) clazz.newInstance();
				ObjectFactory.registerObjectCreator(creator);
				log.debug("Registered extension {}", ext);
			} catch (Exception e) {
				log.debug("Failed to register extension '{}': {}", ext,
						e.getMessage());
				if (log.isTraceEnabled())
					e.printStackTrace();
			}
		}

	}

	public ProcessContainer(URL url) throws Exception {
		this(url, null);
	}

	/**
	 * This constructor creates a new process-container instance by parsing an
	 * XML document located at the specified URL.
	 * 
	 * @param url
	 * @throws Exception
	 */
	public ProcessContainer(URL url,
			Map<String, ElementHandler> customElementHandler) throws Exception {
		container.add(this);

		LibrariesElementHandler libHandler = new LibrariesElementHandler(
				objectFactory);
		documentHandler.add(libHandler);
		documentHandler.add(new PropertiesHandler());
		documentHandler.add(new SystemPropertiesHandler());

		elementHandler.put("Container-Ref", new ContainerRefElementHandler(
				objectFactory));
		elementHandler.put("Queue", new QueueElementHandler());
		elementHandler.put("Monitor", new MonitorElementHandler(objectFactory,
				processorFactory));
		elementHandler.put("Process", new ProcessElementHandler(objectFactory,
				processorFactory));
		elementHandler.put("Stream", new StreamElementHandler(objectFactory));
		elementHandler.put("Service", new ServiceElementHandler(objectFactory));
		elementHandler.put("Libs", libHandler);

		if (customElementHandler != null)
			elementHandler.putAll(customElementHandler);

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(url.openStream());

		Element root = doc.getDocumentElement();
		Map<String, String> attr = objectFactory.getAttributes(root);

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

		if (!root.getNodeName().equalsIgnoreCase("experiment")
				&& !root.getNodeName().equalsIgnoreCase("container"))
			throw new Exception("Expecting root element to be 'container'!");

		String host = InetAddress.getLocalHost().getHostAddress(); // .getHostName();
		name = InetAddress.getLocalHost().getHostName();
		if (name.indexOf(".") > 0) {
			name = name.substring(0, name.indexOf("."));
		}

		log.debug("Default hostname is: {}", host);
		// String host = "localhost";
		if (attr.containsKey("address")
				&& !attr.get("address").trim().isEmpty()) {
			host = InetAddress.getByName(attr.get("address")).getHostAddress();
			log.debug("Container address will be {}", host);
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
				namingService = (NamingService) objectFactory.create(nsClass,
						attr, ObjectFactory.createConfigDocument(root));
		} catch (Exception e) {
			log.error("Faild to instantiate naming service '{}': {}",
					root.getAttribute("namingService"), e.getMessage());
			throw new Exception("Faild to instantiate naming service '"
					+ root.getAttribute("namingService") + "': "
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
				log.debug("No address specified, using local naming-service. Container will not be able to reference other containers!");
				namingService = new DefaultNamingService();
			}
		}

		if (namingService instanceof LifeCycle) {
			lifeCyleObjects.add((LifeCycle) namingService);
		}

		log.debug("Using naming-service {}", namingService);
		context = new ContainerContext(name, namingService);
		this.init(doc);
	}

	/**
	 * @see stream.runtime.IContainer#computeGraph()
	 */
	@Override
	public ComputeGraph computeGraph() {
		return depGraph;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see stream.runtime.IContainer#getStreams()
	 */
	@Override
	public Set<Source> getStreams() {
		return new LinkedHashSet<Source>(this.streams.values());
	}

	/*
	 * (non-Javadoc)
	 * 
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

		NodeList children = root.getChildNodes();

		if (context.getProperties().get("container.datafactory") != null) {
			log.debug("Using {} as default DataFactory for this container...",
					context.getProperties().get("container.datafactory"));
			Class<?> dataFactoryClass = Class.forName(context.getProperties()
					.get("container.datafactory"));
			DataFactory.setDefaultDataFactory((DataFactory) dataFactoryClass
					.newInstance());
		}

		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);

			if (node.getNodeType() != Node.ELEMENT_NODE)
				continue;

			Element element = (Element) node;
			for (ElementHandler handler : this.elementHandler.values()) {
				if (handler.handlesElement(element)) {
					handler.handleElement(this, element, containerVariables,
							dependencyInjection);
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

		// Special Treatment for properties
		DocumentHandler ph = new PropertiesHandler();
		Variables pv = new Variables();
		ph.handle(this, doc, pv, dependencyInjection);
		context.getProperties().putAll(pv);

	}

	public void registerQueue(String id, Queue queue, boolean externalListener)
			throws Exception {
		log.debug("A new queue '{}' is registered for id '{}'", queue, id);
		// if (externalListener) {
		// listeners.put(id, queue);
		// }
		setStream(id, queue);
		// context.register(id, queue);
	}

	public void setStream(String id, Source stream) {
		streams.put(id, stream);
	}

	public long run() throws Exception {

		if (!container.contains(this)) {
			container.add(this);
		}

		startTime = System.currentTimeMillis();
		ContainerController controller = new ContainerController(this);
		log.debug("Registering container-controller {}", controller);
		this.namingService.register(".ctrl", controller);

		// this.injectServices();

		if (!server && streams.isEmpty() && listeners.isEmpty())
			throw new Exception("No data-stream defined!");

		log.debug("Need to handle {} sources: {}", streams.size(),
				streams.keySet());

		log.debug("Experiment contains {} stream processes", processes.size());

		log.debug("Initializing all DataStreams...");
		for (String name : streams.keySet()) {
			Source stream = streams.get(name);
			log.debug("Initializing stream '{}'", name);
			stream.init();
		}

		log.debug("Creating {} active processes...", processes.size());
		long start = System.currentTimeMillis();
		for (Process spu : processes) {

			ProcessContext ctx = this.processContexts.get(spu);
			if (ctx == null) {
				ctx = new ProcessContextImpl(context);
				processContexts.put(spu, ctx);
			}
			// log.debug("Initializing process with process-context...");
			// spu.init(ctx);

			ProcessThread worker = new ProcessThread(spu, ctx);
			worker.addListener(new ProcessListener() {
				@Override
				public void processStarted(Process p) {
				}

				@Override
				public void processFinished(Process p) {
					depGraph.remove(p);
				}
			});

			log.debug("Initializing stream-process [{}]", spu);
			worker.init();

			log.debug("Starting stream-process [{}]", spu);
			worker.start();
			log.debug("Stream-process started.");
		}

		log.debug("Waiting for container to finish...");
		ShutdownCondition con = null;

		if (server)
			con = new ServerShutdownCondition();
		else
			con = new LocalShutdownCondition();

		log.debug("Waiting for shutdown-condition...");
		con.waitForCondition(depGraph);
		log.debug("Shutdown-condition met, container finished.");

		// if the shutdown condition has properly been reached (no Ctrl+C), then
		// we do not require the shutdown hook to be run...
		ProcessContainer.runShutdownHook = false;

		long end = System.currentTimeMillis();
		log.trace("Running processes: {}", processes);
		log.debug("ProcessContainer finished all processes after {} ms",
				(end - start));
		return end - start;
	}

	/*
	 * (non-Javadoc)
	 * 
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
				log.error(
						"Failed to inject arriving data item into queue {}: {}",
						key, e.getMessage());
			}
		} else {
			log.warn("No listener defined for {}", key);
		}
	}

	public void shutdown() {

		synchronized (runShutdownHook) {
			if (!runShutdownHook)
				return;

			// ensure that the shutdown hook is only run *once*
			runShutdownHook = false;
		}

		log.debug("Graph has {} source elements", depGraph.getRootSources()
				.size());
		for (Object source : depGraph.getRootSources()) {
			log.debug("Removing element {} from compute-graph", source);
			List<LifeCycle> lifeCycles = depGraph.remove(source);
			for (LifeCycle lf : lifeCycles) {
				try {
					log.info("Finishing LifeCycle object {}", lf);
					lf.finish();
				} catch (Exception e) {
					log.error("Failed to end LifeCycle object {}: {}", lf,
							e.getMessage());
					if (log.isDebugEnabled())
						e.printStackTrace();
				}
			}
		}
	}

	/**
	 * @param process
	 * @param ctx
	 */
	public void setProcessContext(DefaultProcess process, ProcessContext ctx) {
		processContexts.put(process, ctx);
	}
}