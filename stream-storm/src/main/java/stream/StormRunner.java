/**
 * 
 */
package stream;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringWriter;
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

import stream.util.XMLUtils;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.utils.Utils;

/**
 * @author chris
 * 
 */
public class StormRunner {

	static Logger log = LoggerFactory.getLogger(StormRunner.class);
	public final static String UUID_ATTRIBUTE = "stream.storm.uuid";

	public static void addUUIDAttributes(Element element) {

		UUID id = UUID.randomUUID();
		element.setAttribute(UUID_ATTRIBUTE, id.toString());

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

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		InputStream in = null;
		if (args.length > 0) {
			File xml = new File(args[0]);
			in = new FileInputStream(xml);
		} else {
			in = StormRunner.class.getResourceAsStream("/example.xml");
		}

		long start = System.currentTimeMillis();
		String xml = createIDs(in);
		long end = System.currentTimeMillis();

		log.info("Creating XML took {}", (end - start));
		log.info("XML result is:\n{}", xml);

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
		conf.setDebug(true);

		StreamTopology st = StreamTopology.create(doc);

		if (System.getProperty("local") == null) {
			conf.put(Config.NIMBUS_HOST, "192.168.10.10");
			conf.put(Config.NIMBUS_THRIFT_PORT, "6627");
			StormSubmitter.submitTopology("test", conf, st.createTopology());
		} else {
			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology("test", conf, st.createTopology());

			log.info("Topology submitted.");
			Utils.sleep(10000000);

			cluster.killTopology("test");
			cluster.shutdown();
		}
	}
}