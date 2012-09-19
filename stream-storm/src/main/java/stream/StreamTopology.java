/**
 * 
 */
package stream;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import stream.io.TimeStream;
import stream.storm.ClockSpout;
import stream.storm.MonitorBolt;
import stream.storm.ProcessBolt;
import stream.storm.StreamSpout;
import stream.util.XMLUtils;
import stream.util.parser.TimeParser;
import backtype.storm.Config;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.StormTopology;
import backtype.storm.topology.TopologyBuilder;

/**
 * @author chris
 * 
 */
public class StreamTopology {

	public final static String UUID_ATTRIBUTE = "stream.storm.uuid";
	static Logger log = LoggerFactory.getLogger(StreamTopology.class);

	public static StormTopology createTopology(Document doc) throws Exception {

		doc = XMLUtils.addUUIDAttributes(doc, UUID_ATTRIBUTE);

		String xml = XMLUtils.toString(doc);
		TopologyBuilder builder = new TopologyBuilder();

		// a map of pre-defined streams...
		//
		// Map<String, String> streams = new LinkedHashMap<String, String>();

		NodeList list = doc.getDocumentElement().getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {

			Node node = list.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				log.info(node.getNodeName());
				Element el = (Element) node;
				String uuid = el.getAttribute(UUID_ATTRIBUTE);

				if (el.getNodeName().equalsIgnoreCase("stream")) {
					String id = el.getAttribute("id");
					log.info("Creating stream-spout for id {}", id);
					builder.setSpout(id, new StreamSpout(xml, uuid));
					continue;
				}

				if (el.getNodeName().equalsIgnoreCase("process")) {
					String input = el.getAttribute("input");
					String copies = el.getAttribute("copies");
					Integer workers = 1;
					if (copies != null) {
						try {

						} catch (Exception e) {
							workers = 1;
							throw new RuntimeException(
									"Invalid number of copies '" + copies
											+ "' specified!");
						}
					}

					builder.setBolt(uuid, new ProcessBolt(xml, uuid), workers)
							.shuffleGrouping(input);
				}

				if (el.getNodeName().equalsIgnoreCase("monitor")) {

					String clock = "clock:" + UUID.randomUUID().toString();
					String interval = el.getAttribute("interval");
					TimeStream timeStream = new TimeStream();
					timeStream.setInterval(interval);

					ClockSpout spout = new ClockSpout(
							TimeParser.parseTime(interval));
					builder.setSpout(clock, spout);
					builder.setBolt(uuid, new MonitorBolt(xml, uuid))
							.shuffleGrouping(clock);
				}
			}
		}

		return builder.createTopology();
	}

	public static void main(String[] args) throws Exception {

		if (args.length != 1) {
			System.err.println("Missing XML definition (base64 encoded)!");
			return;
		}

		Document doc = DocumentEncoder.decodeDocument(args[0]);
		Config conf = new Config();
		conf.setNumWorkers(20);

		StormSubmitter.submitTopology("test", conf, createTopology(doc));
	}
}