/**
 * 
 */
package storm;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import stream.DocumentEncoder;
import stream.StreamTopology;
import stream.util.XMLUtils;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.generated.StormTopology;
import backtype.storm.utils.Utils;

/**
 * @author chris
 * 
 */
public class run {

	static Logger log = LoggerFactory.getLogger(run.class);
	public final static String UUID_ATTRIBUTE = "id";
	private static LocalCluster localCluster;

	public static void addUUIDAttributes(Element element) {

		String theId = element.getAttribute("id");
		if (theId == null || theId.trim().isEmpty()) {
			UUID id = UUID.randomUUID();
			element.setAttribute(UUID_ATTRIBUTE, id.toString());
		}

		NodeList list = element.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				addUUIDAttributes((Element) node);
			}
		}
	}

	public static String createIDs(InputStream in) throws Exception {

		DocumentBuilder builder = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();
		Document doc = builder.parse(in);

		addUUIDAttributes(doc.getDocumentElement());

		Transformer trans = TransformerFactory.newInstance().newTransformer();
		Source source = new DOMSource(doc);
		StringWriter out = new StringWriter();
		Result output = new StreamResult(out);
		trans.transform(source, output);

		String xml = out.toString();
		return xml;
	}

	public static Element findElementByUUID(Element el, String uuid) {
		String id = el.getAttribute(UUID_ATTRIBUTE);
		if (uuid.equals(id)) {
			return el;
		}

		NodeList list = el.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {

				Element found = findElementByUUID((Element) node, uuid);
				if (found != null)
					return found;
			}
		}

		return null;
	}

	public static void main(URL url) throws Exception {

		ShutdownHook shutdown = new ShutdownHook();
		Runtime.getRuntime().addShutdownHook(shutdown);

		InputStream in = url.openStream();

		String xml = createIDs(in);
		Document doc = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder()
				.parse(new ByteArrayInputStream(xml.getBytes()));

		doc = XMLUtils.parseDocument(xml);
		doc = XMLUtils.addUUIDAttributes(doc, UUID_ATTRIBUTE);

		log.info("Encoding document...");
		String enc = DocumentEncoder.encodeDocument(doc);
		log.info("Arg will be:\n{}", enc);

		Document decxml = DocumentEncoder.decodeDocument(enc);
		log.info("Decoded XML is: {}", XMLUtils.toString(decxml));

		if (enc == null)
			return;

		Config conf = new Config();
		conf.setDebug(false);

		StreamTopology st = StreamTopology.create(doc);

		log.info("Creating stream-topology...");

		StormTopology storm = st.createTopology();

		log.info("Starting local cluster...");
		LocalCluster cluster = startLocalCluster();

		log.info("########################################################################");
		log.info("submitting topology...");
		cluster.submitTopology(
				System.getProperty("id", UUID.randomUUID().toString()), conf,
				storm);
		log.info("########################################################################");

		log.info("Topology submitted.");

		Utils.sleep(Long.MAX_VALUE);
	}

	public static LocalCluster getLocalCluster() {
		return localCluster;
	}

	public static LocalCluster startLocalCluster() {
		if (localCluster != null) {
			log.info("Local cluster {} already running...", localCluster);
			return localCluster;
		}

		localCluster = new LocalCluster();
		return localCluster;
	}

	public static void stopLocalCluster() {
		if (localCluster != null) {
			localCluster.shutdown();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		List<String> params = storm.deploy.handleArgs(args);

		if (params.isEmpty()) {
			System.err.println("You need to specify an XML configuration!");
			System.exit(-1);
		}

		File file = new File(params.get(0));
		main(file.toURI().toURL());
	}

	public static class ShutdownHook extends Thread {

		private Set<String> topologies = new LinkedHashSet<String>();

		public void addTopology(String name) {
			topologies.add(name);
		}

		public void run() {

			if (storm.run.getLocalCluster() == null) {
				log.info("No local cluster started, nothing to shut down...");
				return;
			}

			for (String topo : topologies) {
				log.info("Killing topology '{}'", topo);
				storm.run.getLocalCluster().killTopology(topo);
			}

			log.info("Shutting down local cluster...");
			storm.run.stopLocalCluster();
		}
	}
}