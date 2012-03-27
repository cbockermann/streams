package stream.tools;

import java.lang.reflect.Constructor;
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
import stream.data.DataProcessor;
import stream.io.DataStream;
import stream.io.DataStreamProcessor;
import stream.io.DataStreamQueue;
import stream.util.ObjectFactory;
import stream.util.ParameterInjection;

public class StreamRunner {
	static Logger log = LoggerFactory.getLogger(StreamRunner.class);
	ObjectFactory objectFactory = ObjectFactory.newInstance();

	boolean openListeners = true;

	ExperimentContext context = new ExperimentContext();

	Map<String, DataStream> streams = new LinkedHashMap<String, DataStream>();

	/*
	 * This is a set of sinks, one sink may be connected to multiple streams (by
	 * key)
	 */
	Map<String, List<DataProcessor>> processors = new LinkedHashMap<String, List<DataProcessor>>();

	Map<String, DataStreamQueue> listeners = new LinkedHashMap<String, DataStreamQueue>();

	public StreamRunner(URL url) throws Exception {
		this(url, true);
	}

	public StreamRunner(URL url, boolean openListeners) throws Exception {
		this.openListeners = openListeners;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(url.openStream());

		if (!doc.getDocumentElement().getNodeName()
				.equalsIgnoreCase("experiment")) {
			throw new Exception("Expecting root element to be 'experiment'!");
		}

		this.init(doc);
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

		context = new ExperimentContext(name);
		context.getProperties().putAll(getProperties(root));
		objectFactory.addVariables(context.getProperties());
		NodeList children = root.getChildNodes();

		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			String elementName = node.getNodeName();

			if (node instanceof Element
					&& elementName.equalsIgnoreCase("Stream")) {
				Element child = (Element) node;
				try {
					Map<String, String> attr = objectFactory
							.getAttributes(child);
					String id = attr.get("id");

					DataStream stream = createStream(attr);
					if (stream != null) {
						if (id == null)
							id = "" + stream;
						streams.put(id, stream);
					}

					NodeList proc = child.getChildNodes();
					for (int j = 0; j < proc.getLength(); j++) {
						Node n = proc.item(j);
						if (n instanceof Element) {
							try {
								Object object = objectFactory
										.create((Element) n);
								if (object instanceof DataProcessor) {
									stream.addPreprocessor((DataProcessor) object);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}

				} catch (Exception e) {
					log.error("Failed to create object: {}", e.getMessage());
					e.printStackTrace();
				}
			}

			if (node instanceof Element
					&& (elementName.equalsIgnoreCase("Processing"))
					|| elementName.equalsIgnoreCase("Process")) {
				log.debug("Found 'Processing' element!");
				Element child = (Element) node;

				/*
				 */
				DataStreamProcessor proc = new DataStreamProcessor();
				Map<String, String> attr = objectFactory.getAttributes(child);
				String src = attr.get("source");
				if (src == null)
					src = attr.get("input");

				if (src == null) {
					log.error("No input defined for processor-chain {}", node);
				}

				List<DataProcessor> procs = this.getDataProcessors(child);
				log.debug("Adding {} processors to processing-chain",
						procs.size());
				for (DataProcessor p : procs)
					proc.addDataProcessor(p);

				log.debug("Processor [{}] is handling stream '{}'",
						attr.get("id"), src);

				if (src != null) {
					List<DataProcessor> ps = this.processors.get(src);
					if (ps == null) {
						ps = new ArrayList<DataProcessor>();
					}
					ps.add(proc);
					log.debug("Adding list of processors for stream '{}': {}",
							src, ps);
					processors.put(src, ps);
				}
			}
		}

		if (openListeners) {
			for (String input : processors.keySet()) {
				if (streams.get(input) == null) {
					log.debug("Creating listener-queue for input-key '{}'",
							input);

					DataStreamQueue q = new DataStreamQueue();
					listeners.put(input, q);
					streams.put(input, q);
					context.register(input, q);
				}
			}
		}

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

	public List<DataProcessor> getDataProcessors(Element child)
			throws Exception {
		List<DataProcessor> processors = new ArrayList<DataProcessor>();
		NodeList proc = child.getChildNodes(); // .getElementsByTagName(
		// "Processor");
		for (int j = 0; j < proc.getLength(); j++) {
			Node n = proc.item(j);
			String name = n.getNodeName();
			if (n instanceof Element) {

				DataProcessor p = null;

				if ((name.equalsIgnoreCase("processor") || name
						.equalsIgnoreCase("mapper"))) {
					try {
						p = (DataProcessor) objectFactory.create((Element) n);
					} catch (ClassNotFoundException cnfe) {
						log.error("Failed to create object for class {}",
								cnfe.getMessage());
						throw cnfe;
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {

					try {
						log.debug("Trying to generate object from {}", n);
						p = (DataProcessor) objectFactory.create((Element) n);
						log.debug("Created generic data-processor {}", p);
					} catch (Exception e) {
						e.printStackTrace();
					}

				}

				if (p != null) {
					log.debug("Adding data processor...");
					processors.add(p);
				}

				String id = ((Element) n).getAttribute("id");
				if (id != null && !"".equals(id.trim())) {
					log.debug("Registering processor with attribute '{}'", id);
					context.register(id, p);
				}
			}
		}
		return processors;
	}

	public static DataStream createStream(Map<String, String> params)
			throws Exception {
		Class<?> clazz = Class.forName(params.get("class"));
		Constructor<?> constr = clazz.getConstructor(URL.class);
		String urlParam = params.get("url");
		URL url = null;

		if (params.get("url").startsWith("classpath:")) {
			String resource = urlParam.substring("classpath:".length());
			log.debug("Looking up resource '{}'", resource);
			url = StreamRunner.class.getResource(resource);
		} else {
			url = new URL(urlParam);
		}

		DataStream stream = (DataStream) constr.newInstance(url);

		ParameterInjection.inject(stream, params);
		return stream;
	}

	public DataStream getStream(String key) {

		DataStream stream = streams.get(key);
		if (stream != null)
			return stream;

		stream = listeners.get(key);
		if (stream == null && openListeners) {
			log.info("Creating new listener-queue for {}", key);
			DataStreamQueue queue = new DataStreamQueue();
			listeners.put(key, queue);
			return queue;
		}

		return stream;
	}

	public void run() throws Exception {

		if (streams.isEmpty() && listeners.isEmpty())
			throw new Exception("No data-stream defined!");

		log.info("Need to handle {} sources: {}", streams.size(),
				streams.keySet());

		List<StreamProcess> processes = new ArrayList<StreamProcess>();

		for (String key : streams.keySet()) {
			DataStream input = getStream(key);
			log.debug("Creating new StreamProcess for stream {}", key);
			log.debug("   process {} is reading from {}", key, input);
			StreamProcess p = new StreamProcess(null, context, input);
			List<DataProcessor> proc = processors.get(key);
			if (proc != null && !proc.isEmpty()) {
				log.debug("Adding {} processors to stream-process {}",
						proc.size(), p);
				for (DataProcessor processor : proc) {
					p.addDataProcessor(processor);
				}

				log.debug("Adding stream-process to the process-list...");
				processes.add(p);
			} else {
				log.debug(
						"No consumer found for stream {}, no process will be created!",
						key);
			}
		}

		log.debug("Experiment contains {} stream processes", processes.size());

		for (StreamProcess spu : processes) {
			log.debug("Starting stream-process [{}]", spu.getProcessId());
			spu.start();
			log.debug("Stream-process started.");
		}

		log.debug("waiting for processes to finish...");
		while (!processes.isEmpty()) {
			log.debug("{} processes running", processes.size());
			Iterator<StreamProcess> it = processes.iterator();
			while (it.hasNext()) {
				StreamProcess p = it.next();
				if (!p.isRunning()) {
					log.debug("Process '{}' is finished.", p.getProcessId());
					log.debug("Removing finished process {}", p.getProcessId());
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
}
