/**
 * 
 */
package stream;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
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
import backtype.storm.topology.BoltDeclarer;
import backtype.storm.topology.SpoutDeclarer;
import backtype.storm.topology.TopologyBuilder;

/**
 * @author chris
 * 
 */
public class StreamTopology {

	public final static String UUID_ATTRIBUTE = "stream.storm.uuid";
	static Logger log = LoggerFactory.getLogger(StreamTopology.class);

	final TopologyBuilder builder;
	final Map<String, BoltDeclarer> bolts = new LinkedHashMap<String, BoltDeclarer>();
	final Map<String, SpoutDeclarer> spouts = new LinkedHashMap<String, SpoutDeclarer>();

	/**
	 * 
	 * @param builder
	 */
	private StreamTopology(TopologyBuilder builder) {
		this.builder = builder;
	}

	public TopologyBuilder getTopologyBuilder() {
		return builder;
	}

	/**
	 * This method returns an unmodifiable map of bolts. The keys of this map
	 * are the bolts' identifiers.
	 * 
	 * @return
	 */
	public Map<String, BoltDeclarer> getBolts() {
		return Collections.unmodifiableMap(bolts);
	}

	/**
	 * This method returns an unmodifiable map of spouts. The keys of this map
	 * are the spouts' identifiers.
	 * 
	 * @return
	 */
	public Map<String, SpoutDeclarer> getSpouts() {
		return Collections.unmodifiableMap(spouts);
	}

	/**
	 * Creates a new instance of a StreamTopology based on the given document.
	 * This also creates a standard TopologyBuilder to build the associated
	 * Storm Topology.
	 * 
	 * @param doc
	 *            The DOM document that defines the topology.
	 * @return
	 * @throws Exception
	 */
	public static StreamTopology create(Document doc) throws Exception {
		return build(doc, new TopologyBuilder());
	}

	/**
	 * Creates a new instance of a StreamTopology based on the given document
	 * and using the specified TopologyBuilder.
	 * 
	 * @param doc
	 * @param builder
	 * @return
	 * @throws Exception
	 */
	public static StreamTopology build(Document doc, TopologyBuilder builder)
			throws Exception {

		final StreamTopology st = new StreamTopology(builder);

		doc = XMLUtils.addUUIDAttributes(doc, UUID_ATTRIBUTE);

		String xml = XMLUtils.toString(doc);

		// a map of pre-defined inputs, i.e. input-names => uuids
		// to catch the case when processes read from queues that have
		// not been explicitly defined (i.e. 'linking bolts')
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

					StreamSpout spout = new StreamSpout(xml, uuid);
					SpoutDeclarer spoutDeclarer = builder.setSpout(id, spout);
					st.spouts.put(id, spoutDeclarer);
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

					log.info("Adding bolt {}, subscribing to {}", uuid, input);

					ProcessBolt bolt = new ProcessBolt(xml, uuid);
					BoltDeclarer boltDeclarer = builder.setBolt(uuid, bolt,
							workers).shuffleGrouping(input);
					st.bolts.put(uuid, boltDeclarer);

					continue;
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

		return st;
	}

	/**
	 * This method creates a new instance of type StormTopology based on the
	 * topology that has been created from the DOM document.
	 * 
	 * @return
	 */
	public StormTopology createTopology() {
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

		StreamTopology streamTop = build(doc, new TopologyBuilder());
		StormTopology topology = streamTop.getTopologyBuilder()
				.createTopology();

		StormSubmitter.submitTopology("test", conf, topology);
	}
}