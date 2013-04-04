/**
 * 
 */
package stream.runtime.setup;

import java.io.InputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import stream.io.SourceURL;
import stream.runtime.Container;
import stream.runtime.ContainerFactory;
import stream.runtime.DefaultNamingService;
import stream.runtime.ElementHandler;
import stream.runtime.ProcessContainer;
import stream.runtime.Variables;
import stream.runtime.setup.handler.ContainerRefElementHandler;
import stream.runtime.setup.handler.DocumentHandler;
import stream.runtime.setup.handler.LibrariesElementHandler;
import stream.runtime.setup.handler.MonitorElementHandler;
import stream.runtime.setup.handler.ProcessElementHandler;
import stream.runtime.setup.handler.PropertiesHandler;
import stream.runtime.setup.handler.QueueElementHandler;
import stream.runtime.setup.handler.ServiceElementHandler;
import stream.runtime.setup.handler.StreamElementHandler;
import stream.service.NamingService;

/**
 * <p>
 * This class implements the container factory for the streams runtime
 * environment.
 * </p>
 * 
 * @author Hendrik Blom, Christian Bockermann
 * 
 */
public class StreamsContainerFactory implements ContainerFactory {

	static Logger log = LoggerFactory.getLogger(StreamsContainerFactory.class);

	/* The object factory used to instantiate objects by reflection */
	protected final ObjectFactory objectFactory = ObjectFactory.newInstance();

	/* The processor factory to create processor instances */
	protected final ProcessorFactory processorFactory = new ProcessorFactory(
			objectFactory);

	/* A list of handlers that process the complete document element */
	protected final List<DocumentHandler> documentHandler = new ArrayList<DocumentHandler>();

	/* The list of element handlers for handling XML elements */
	protected final Map<String, ElementHandler> elementHandler = new HashMap<String, ElementHandler>();

	/**
	 * Creates a new instance of the StreamsContainerFactory with the default
	 * handlers registered.
	 */
	public StreamsContainerFactory() {

		LibrariesElementHandler libHandler = new LibrariesElementHandler(
				objectFactory);
		documentHandler.add(libHandler);
		documentHandler.add(new PropertiesHandler());

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
	}

	/**
	 * @see stream.runtime.ContainerFactory#create(stream.io.SourceURL)
	 */
	@Override
	public Container create(SourceURL url) throws Exception {
		return create(url.openStream());
	}

	/**
	 * @see stream.runtime.ContainerFactory#create(java.io.InputStream)
	 */
	@Override
	public Container create(InputStream in) throws Exception {

		// Was machen wir hier eigentlich?
		//
		// (1) Parsen des XML => DOM
		//
		Document doc = this.parseXML(in);

		//
		// (1,5) Erzeugen des NamingServices
		//
		// NamingService namingService = createNamingService();

		// (1.75) Erzeugen des leeren Containers
		//
		ProcessContainer container = null; // new ProcessContainer();

		// (2) Bauen des Containers:
		// * Call der Document+ElementHandler => fuegen Elemente zu Container
		// hinzu
		//
		container = buildContainer(container, doc);

		// String host = InetAddress.getLocalHost().getHostAddress(); //
		// .getHostName();
		String name = InetAddress.getLocalHost().getHostName();
		if (name.indexOf(".") > 0) {
			name = name.substring(0, name.indexOf("."));
		}

		return null;
	}

	protected Document parseXML(InputStream input) throws Exception {
		log.debug("Parsing XML from inputstream");
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(input);

		if (!doc.getDocumentElement().getNodeName()
				.equalsIgnoreCase("container")) {
			log.error("Invalid XML root-element '{}'", doc.getDocumentElement()
					.getNodeName());
			throw new Exception("Expecting root element to be 'container'!");
		}

		return doc;
	}

	protected NamingService createNamingService() {
		return new DefaultNamingService();
	}

	protected ProcessContainer buildContainer(ProcessContainer container,
			Document doc) throws Exception {

		Variables variables = new Variables();

		Element root = doc.getDocumentElement();
		for (DocumentHandler docHandler : documentHandler) {
			log.debug("Calling document-handler {}", docHandler);
			docHandler.handle(container, doc, variables);
		}

		NodeList nodes = root.getChildNodes();

		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);

			if (node.getNodeType() != Node.ELEMENT_NODE) {
				log.debug("Skipping non-element node '{}'", node);
				continue;
			}

			Element element = (Element) node;
			for (String key : elementHandler.keySet()) {
				ElementHandler handler = elementHandler.get(key);
				log.debug("Calling ElementHandler '{}' ({})", key, handler);
				if (handler.handlesElement(element)) {
					handler.handleElement(container, element, new Variables(
							variables));
					continue;
				}
			}
		}

		return container;
	}

	public void addElementHandler(String name, ElementHandler handler) {
		this.elementHandler.put(name, handler);
	}
}
