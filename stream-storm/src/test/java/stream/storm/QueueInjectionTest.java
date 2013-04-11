/**
 * 
 */
package stream.storm;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

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
public class QueueInjectionTest extends TestCase {

	static Logger log = LoggerFactory.getLogger(QueueInjectionTest.class);

	public void test() {

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
		conf.setDebug(false);

		StreamTopology st = StreamTopology.create(doc);

		log.info("Creating stream-topology...");

		StormTopology storm = st.createTopology();

		log.info("Starting local cluster...");
		LocalCluster cluster = new LocalCluster();

		log.info("########################################################################");
		log.info("submitting topology...");
		cluster.submitTopology("test", conf, storm);
		log.info("########################################################################");

		log.info("Topology submitted.");
		Utils.sleep(10000000);

		log.info("########################################################################");
		log.info("killing topology...");
		cluster.killTopology("test");
		cluster.shutdown();
	}
}
