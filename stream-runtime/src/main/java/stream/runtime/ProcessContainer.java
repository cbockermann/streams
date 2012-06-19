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
import java.util.Iterator;
import java.util.LinkedHashMap;
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

import stream.ProcessContext;
import stream.data.Data;
import stream.data.DataFactory;
import stream.io.BlockingQueue;
import stream.io.DataStream;
import stream.io.DataStreamQueue;
import stream.runtime.rpc.RMINamingService;
import stream.runtime.setup.DocumentHandler;
import stream.runtime.setup.LibrariesElementHandler;
import stream.runtime.setup.MonitorElementHandler;
import stream.runtime.setup.ObjectFactory;
import stream.runtime.setup.ProcessElementHandler;
import stream.runtime.setup.ProcessorFactory;
import stream.runtime.setup.PropertiesHandler;
import stream.runtime.setup.ServiceElementHandler;
import stream.runtime.setup.ServiceInjection;
import stream.runtime.setup.ServiceReference;
import stream.runtime.setup.StreamElementHandler;
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
public class ProcessContainer {

	static Logger log = LoggerFactory.getLogger(ProcessContainer.class);

	protected final ObjectFactory objectFactory = ObjectFactory.newInstance();
	protected final ProcessorFactory processorFactory = new ProcessorFactory(
			objectFactory);

	/**
	 * The name of this container, used in lookup URIs (e.g.
	 * //container-name/serice-name )
	 */
	protected String name = null;

	/** The global container context */
	protected final ContainerContext context;

	/** The set of data streams (sources) */
	protected final Map<String, DataStream> streams = new LinkedHashMap<String, DataStream>();

	/** The list of data-stream-queues, that can be fed from external instances */
	protected final Map<String, DataStreamQueue> listeners = new LinkedHashMap<String, DataStreamQueue>();

	/** The list of processes running in this container */
	protected final List<AbstractProcess> processes = new ArrayList<AbstractProcess>();

	protected final List<ServiceReference> serviceRefs = new ArrayList<ServiceReference>();

	protected final Map<String, ElementHandler> elementHandler = new HashMap<String, ElementHandler>();

	protected final List<DocumentHandler> documentHandler = new ArrayList<DocumentHandler>();

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

		LibrariesElementHandler libHandler = new LibrariesElementHandler(
				objectFactory);
		documentHandler.add(libHandler);
		documentHandler.add(new PropertiesHandler());

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
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(url.openStream());

		Element root = doc.getDocumentElement();
		Map<String, String> attr = objectFactory.getAttributes(root);

		if (!root.getNodeName().equalsIgnoreCase("experiment")
				&& !root.getNodeName().equalsIgnoreCase("container"))
			throw new Exception("Expecting root element to be 'container'!");

		String host = InetAddress.getLocalHost().getHostAddress(); // .getHostName();
		name = InetAddress.getLocalHost().getHostName();
		if (name.indexOf(".") > 0) {
			name = name.substring(0, name.indexOf("."));
		}

		log.info("Default hostname is: {}", host);
		// String host = "localhost";
		if (attr.containsKey("address")
				&& !attr.get("address").trim().isEmpty()) {
			host = InetAddress.getByName(attr.get("address")).getHostAddress();
			log.info("Container address will be {}", host);
		}

		Integer port = 9105;
		if (attr.containsKey("port") && !attr.get("port").trim().isEmpty()) {
			port = new Integer(attr.get("port"));
			log.info("Container port will be {}", port);
		}
		if (root.hasAttribute("id"))
			name = root.getAttribute("id");

		NamingService namingService = null;

		try {
			String nsClass = root.getAttribute("namingService");
			if (nsClass != null && !nsClass.trim().isEmpty())
				namingService = (NamingService) objectFactory.create(nsClass,
						attr);
		} catch (Exception e) {
			log.error("Faild to instantiate naming service '{}': {}",
					root.getAttribute("namingService"), e.getMessage());
			e.printStackTrace();
			throw new Exception("Faild to instantiate naming service '"
					+ root.getAttribute("namingService") + "': "
					+ e.getMessage());
		}

		//
		// create the default NamingService if none has been specified, yet.
		//
		if (namingService == null) {

			if (attr.containsKey("address")) {
				System.setProperty("java.rmi.server.hostname", host);
				namingService = new RMINamingService(name, host, port, true);
			} else {
				log.info("No address specified, using local naming-service. Container will not be able to reference other containers!");
				namingService = new DefaultNamingService();
			}
		}

		log.info("Using naming-service {}", namingService);
		context = new ContainerContext(name, namingService);
		this.init(doc);
	}

	/**
	 * @return the name
	 */
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

	public ContainerContext getContext() {
		return context;
	}

	/**
	 * @return the processes
	 */
	public List<AbstractProcess> getProcesses() {
		return processes;
	}

	/**
	 * @return the serviceRefs
	 */
	public List<ServiceReference> getServiceRefs() {
		return serviceRefs;
	}

	private void init(Document doc) throws Exception {
		Element root = doc.getDocumentElement();

		if (root.getAttribute("import") != null) {
			String[] pkgs = root.getAttribute("import").split(",");
			for (String pkg : pkgs) {
				if (!pkg.trim().isEmpty())
					objectFactory.addPackage(pkg);
			}
		}

		String name = root.getAttribute("name");
		if (name == null)
			name = "local";

		for (DocumentHandler handle : documentHandler) {
			handle.handle(this, doc);
		}
		objectFactory.addVariables(context.getProperties());

		NodeList children = root.getChildNodes();

		if (context.getProperties().get("container.datafactory") != null) {
			log.info("Using {} as default DataFactory for this container...",
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
					handler.handleElement(this, element);
					continue;
				}
			}

		}

		connectProcesses();

		injectServices();
	}

	/**
	 * 
	 */
	protected void connectProcesses() throws Exception {
		log.debug("Wiring process inputs to data-streams...");
		for (AbstractProcess aprocess : processes) {

			if (aprocess instanceof Process) {
				Process process = (Process) aprocess;
				String input = process.getInput();
				if (input == null) {
					throw new RuntimeException("Process '" + process
							+ "' is not connected to any input-stream!");
				}

				DataStream stream = streams.get(input);
				if (stream == null) {
					log.debug(
							"No stream defined for name '{}' - creating a listener-queue for key '{}'",
							input, input);
					DataStreamQueue q = new BlockingQueue();
					listeners.put(input, q);
					setStream(input, q);
					context.register(input, q);
					stream = q;
				}

				process.setDataStream(stream);
			}
		}
	}

	protected void injectServices() throws Exception {
		ServiceInjection.injectServices(this.getServiceRefs(),
				this.getContext());
	}

	public void setStream(String id, DataStream stream) {
		streams.put(id, stream);
	}

	public void run() throws Exception {

		if (streams.isEmpty() && listeners.isEmpty())
			throw new Exception("No data-stream defined!");

		log.debug("Need to handle {} sources: {}", streams.size(),
				streams.keySet());

		log.debug("Experiment contains {} stream processes", processes.size());

		log.debug("Initializing all DataStreams...");
		for (String name : streams.keySet()) {
			DataStream stream = streams.get(name);
			log.debug("Initializing stream '{}'", name);
			stream.init();
		}

		log.debug("Creating {} active processes...", processes.size());
		long start = System.currentTimeMillis();
		for (AbstractProcess spu : processes) {
			spu.setDaemon(true);

			ProcessContext ctx = new ProcessContextImpl(context);
			log.debug("Initializing process with process-context...");
			spu.init(ctx);

			log.debug("Starting stream-process [{}]", spu);
			spu.start();
			log.debug("Stream-process started.");
		}

		Thread.sleep(1000);

		log.debug("waiting for processes to finish...");
		while (!processes.isEmpty()) {
			log.trace("{} processes running", processes.size());
			Iterator<AbstractProcess> it = processes.iterator();
			while (it.hasNext()) {
				AbstractProcess p = it.next();
				if (!p.isRunning() || p instanceof Monitor) {
					log.debug("Process '{}' is finished.", p);
					log.debug("Removing finished process {}", p);
					it.remove();
				} else {
					log.trace("    {} is still running", p);
				}
			}
			try {
				Thread.sleep(500);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		long end = System.currentTimeMillis();
		log.info("ProcessContainer finished all processes after about {} ms",
				(end - start));
	}

	public Set<String> getStreamListenerNames() {
		return listeners.keySet();
	}

	public ObjectFactory getObjectFactory() {
		return this.objectFactory;
	}

	public void dataArrived(String key, Data item) {
		if (listeners.containsKey(key)) {
			listeners.get(key).process(item);
		} else {
			log.warn("No listener defined for {}", key);
		}
	}

	public void shutdown() {
		synchronized (processes) {
			for (AbstractProcess process : processes) {
				log.debug("Sending SHUTDOWN signal to process {}", process);
				try {
					process.finish();
				} catch (Exception e) {
					log.error("Failed to properly shutdown process: {}",
							e.getMessage());
				}
			}
		}

		while (!processes.isEmpty()) {
			try {
				log.info("Waiting for processes to finish...");
				Thread.sleep(500);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
