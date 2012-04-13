package stream.runtime;

import java.net.URL;
import java.util.ArrayList;
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

import stream.Processor;
import stream.data.Data;
import stream.data.DataProcessorList;
import stream.io.DataStream;
import stream.io.DataStreamQueue;
import stream.runtime.setup.DataStreamFactory;
import stream.runtime.setup.ObjectFactory;
import stream.runtime.setup.ProcessorFactory;
import stream.runtime.setup.ServiceInjection;
import stream.runtime.setup.ServiceReference;
import stream.service.Service;

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

	/**
	 * This constructor creates a new process-container instance by parsing an
	 * XML document located at the specified URL.
	 * 
	 * @param url
	 * @throws Exception
	 */
	public ProcessContainer(URL url) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(url.openStream());

		Element root = doc.getDocumentElement();

		if (!root.getNodeName().equalsIgnoreCase("experiment")
				&& !root.getNodeName().equalsIgnoreCase("container")) {
			throw new Exception("Expecting root element to be 'container'!");
		}

		if (root.hasAttribute("id")) {
			name = root.getAttribute("id");
		} else {
			name = "local";
		}

		context = new ContainerContext(name);
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
				if (!pkg.trim().isEmpty()) {
					objectFactory.addPackage(pkg);
				}
			}
		}

		String name = root.getAttribute("name");
		if (name == null) {
			name = "local";
		}

		context.getProperties().putAll(getProperties(root));
		NodeList children = root.getChildNodes();

		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);

			if (node.getNodeType() != Node.ELEMENT_NODE)
				continue;

			Element element = (Element) node;
			String elementName = node.getNodeName();

			if (elementName.equalsIgnoreCase("Stream")
					|| elementName.equalsIgnoreCase("DataStream")) {
				try {
					Map<String, String> attr = objectFactory
							.getAttributes(element);
					String id = attr.get("id");

					DataStream stream = DataStreamFactory.createStream(
							objectFactory, processorFactory, element);
					if (stream != null) {
						if (id == null)
							id = "" + stream;
						streams.put(id, stream);
					}

				} catch (Exception e) {
					log.error("Failed to create object: {}", e.getMessage());
					e.printStackTrace();
				}
				continue;
			}

			if ("monitor".equalsIgnoreCase(elementName)) {
				Map<String, String> params = objectFactory
						.getAttributes(element);

				// the default Monitor class is stream.runtime.Monitor
				//
				String className = "stream.runtime.Monitor";
				if (element.hasAttribute("class")) {
					className = element.getAttribute("class");
					log.info(
							"Creating Monitor instance from custom class '{}'",
							className);
				}

				Monitor monitor = (Monitor) objectFactory.create(className,
						params);
				log.debug("Created Monitor object: {}", monitor);

				List<Processor> procs = createNestedProcessors(element);
				for (Processor p : procs)
					monitor.addProcessor(p);

				processes.add(monitor);
				continue;
			}

			if ((elementName.equalsIgnoreCase("Processing"))
					|| elementName.equalsIgnoreCase("Process")) {
				log.debug("Found 'Processing' element!");
				Element child = (Element) node;

				Map<String, String> attr = objectFactory.getAttributes(child);
				String src = attr.get("source");
				if (src == null)
					src = attr.get("input");

				// Create the default data-stream process
				//
				String processClass = "stream.runtime.Process";
				if (attr.containsKey("class")) {
					processClass = attr.get("class");
					log.info("Using custom process class '{}'", processClass);
				}

				Process process = (Process) objectFactory.create(processClass,
						attr);
				log.debug("Created Process object: {}", process);

				List<Processor> procs = createNestedProcessors(child);
				for (Processor p : procs) {
					process.addProcessor(p);
				}
				processes.add(process);
			}
		}

		connectProcesses();

		ServiceInjection.injectServices(this.getServiceRefs(),
				this.getContext());
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
					DataStreamQueue q = new DataStreamQueue();
					listeners.put(input, q);
					streams.put(input, q);
					context.register(input, q);
					stream = q;
				}

				process.setDataStream(stream);
			}
		}
	}

	protected List<Processor> createNestedProcessors(Element child)
			throws Exception {
		List<Processor> procs = new ArrayList<Processor>();

		NodeList pnodes = child.getChildNodes();
		for (int j = 0; j < pnodes.getLength(); j++) {

			Node cnode = pnodes.item(j);
			if (cnode.getNodeType() == Node.ELEMENT_NODE) {
				Processor p = createProcessor((Element) cnode);
				if (p != null) {
					log.debug("Found processor...");
					procs.add(p);
				}
			}
		}
		return procs;
	}

	protected Map<String, String> getProperties(Element element) {
		Map<String, String> props = new LinkedHashMap<String, String>();
		NodeList ch = element.getChildNodes();
		for (int i = 0; i < ch.getLength(); i++) {
			Node child = ch.item(i);
			if (child instanceof Element) {
				Element el = (Element) child;
				if (el.getNodeName().equalsIgnoreCase("property")) {

					String key = el.getAttribute("name");
					String value = el.getAttribute("value");

					if (key != null && !"".equals(key.trim()) && value != null
							&& !"".equals(value.trim())) {
						props.put(key, value);
					}
				}
			}
		}
		log.debug("Found properties: {}", props);
		return props;
	}

	protected Processor createProcessor(Element child) throws Exception {

		Map<String, String> params = objectFactory.getAttributes(child);

		Object o = objectFactory.create(child);
		if (o instanceof Processor) {

			if (o instanceof DataProcessorList) {

				NodeList children = child.getChildNodes();
				for (int i = 0; i < children.getLength(); i++) {

					Node node = children.item(i);
					if (node.getNodeType() == Node.ELEMENT_NODE) {

						Element element = (Element) node;
						Processor proc = createProcessor(element);
						if (proc != null) {
							((DataProcessorList) o).addDataProcessor(proc);
						} else {
							log.warn(
									"Nested element {} is not of type 'stream.data.Processor': ",
									node.getNodeName());
						}
					}
				}
			}

			if (params.containsKey("id") && !"".equals(params.get("id").trim())) {

				if (o instanceof Service) {
					String id = params.get("id").trim();
					log.debug(
							"Registiering processor with id '{}' in look-up service",
							child.getAttribute("id"));
					context.register(id, (Service) o);
				} else {
					log.warn(
							"Processor '{}' specifies an ID attribute '{}' but does not implement a Service interface. Processor will *not* be registered!",
							o.getClass().getName(), params.get("id"));
				}
			}

			for (String key : params.keySet()) {

				if (key.endsWith("-ref")) {
					String ref = params.get(key);
					ServiceReference serviceRef = new ServiceReference(ref, o,
							key);
					serviceRefs.add(serviceRef);
				}
			}

			return (Processor) o;
		}

		return null;
	}

	public void run() throws Exception {

		if (streams.isEmpty() && listeners.isEmpty())
			throw new Exception("No data-stream defined!");

		log.debug("Need to handle {} sources: {}", streams.size(),
				streams.keySet());

		log.debug("Experiment contains {} stream processes", processes.size());

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
			log.debug("{} processes running", processes.size());
			Iterator<AbstractProcess> it = processes.iterator();
			while (it.hasNext()) {
				AbstractProcess p = it.next();
				if (!p.isRunning()) {
					log.debug("Process '{}' is finished.", p);
					log.debug("Removing finished process {}", p);
					it.remove();
				} else {
					log.debug("    {} is still running", p);
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

	public void dataArrived(String key, Data item) {
		if (listeners.containsKey(key)) {
			listeners.get(key).dataArrived(item);
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