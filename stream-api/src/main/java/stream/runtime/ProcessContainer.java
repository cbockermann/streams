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

import stream.data.Data;
import stream.data.DataProcessorList;
import stream.data.Processor;
import stream.io.DataStream;
import stream.io.DataStreamProcessor;
import stream.io.DataStreamQueue;
import stream.runtime.setup.DataStreamFactory;
import stream.runtime.setup.ObjectFactory;

public class ProcessContainer {

	static Logger log = LoggerFactory.getLogger(ProcessContainer.class);

	final ObjectFactory objectFactory = ObjectFactory.newInstance();

	String name = null;

	ContainerContext context = new ContainerContext();

	Map<String, DataStream> streams = new LinkedHashMap<String, DataStream>();

	Map<String, DataStreamQueue> listeners = new LinkedHashMap<String, DataStreamQueue>();

	final List<AbstractProcess> processes = new ArrayList<AbstractProcess>();

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
		}

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

	public Context getContext() {
		return context;
	}

	public void init(Document doc) throws Exception {
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

		context = new ContainerContext(name);
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

					DataStream stream = DataStreamFactory.createStream(attr);
					if (stream != null) {
						if (id == null)
							id = "" + stream;
						streams.put(id, stream);
					}

					List<Processor> preProcessors = this
							.createNestedProcessors(element);
					for (Processor p : preProcessors) {
						stream.addPreprocessor(p);
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
				String className = "stream.runtime.Monitor";
				if (element.hasAttribute("class"))
					className = element.getAttribute("class");

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

				// Create the default data-stream process
				//
				DataStreamProcessor proc = new DataStreamProcessor();

				if (child.hasAttribute("class")) {
					//
					// optionally, a custom process implementation can be
					// provided
					//
					Map<String, String> parameters = objectFactory
							.getAttributes(child);
					proc = (DataStreamProcessor) objectFactory.create(
							child.getAttribute("class"), parameters);
				}

				Map<String, String> attr = objectFactory.getAttributes(child);
				String src = attr.get("source");
				if (src == null)
					src = attr.get("input");

				Process process = (Process) objectFactory.create(
						"stream.runtime.Process", attr);
				log.debug("Created Process object: {}", process);

				List<Processor> procs = this.createNestedProcessors(child);
				for (Processor p : procs) {
					proc.addDataProcessor(p);
					process.addProcessor(p);
				}
				processes.add(process);
			}
		}

		connectProcesses();
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

	public List<Processor> createNestedProcessors(Element child)
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

	public Map<String, String> getProperties(Element element) {
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

	private Processor createProcessor(Element child) throws Exception {

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

			if (child.hasAttribute("id")
					&& !"".equals(child.getAttribute("id").trim())) {
				log.debug(
						"Registiering processor with id '{}' in look-up service",
						child.getAttribute("id"));
				context.register(child.getAttribute("id").trim(), (Processor) o);
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

		for (AbstractProcess spu : processes) {
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
				if (!p.isRunning()) {
					log.debug("Process '{}' is finished.", p);
					log.debug("Removing finished process {}", p);
					it.remove();
				}
			}
			try {
				Thread.sleep(500);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
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