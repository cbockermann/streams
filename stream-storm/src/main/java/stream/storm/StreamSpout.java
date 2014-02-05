/**
 * 
 */
package stream.storm;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import stream.Data;
import stream.io.Stream;
import stream.runtime.setup.factory.ObjectFactory;
import stream.runtime.setup.factory.StreamFactory;
import stream.storm.config.StreamHandler.StreamFinder;
import stream.util.Variables;
import stream.util.XMLUtils;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

/**
 * @author Christian Bockermann
 * 
 */
public class StreamSpout extends BaseRichSpout {

	/** The unique class ID */
	private static final long serialVersionUID = -786482575770711600L;

	static Logger log = LoggerFactory.getLogger(StreamSpout.class);

	transient Stream stream;
	protected SpoutOutputCollector output;

	// the implementing class of the stream
	protected final String className;
	protected final Variables parameters;
	protected final String xml;
	protected final String id;

	public StreamSpout(String xml, String id, String className,
			Map<String, String> params) throws Exception {
		log.debug("Creating spout for stream (class: {}, params: {})",
				className, params);
		this.xml = xml;
		this.id = id;
		this.className = className;
		this.parameters = new Variables(params);
		stream = createStream();
	}

	protected Stream createStream() throws Exception {
		Stream stream = null;

		Document doc = XMLUtils.parseDocument(xml);
		List<Element> els = XMLUtils.findElements(doc, new StreamFinder(id));

		if (els.size() != 1) {
			throw new RuntimeException(
					"Failed to locate 'stream' element for id '" + id + "'!");
		}

		Element el = els.get(0);
		ObjectFactory objectFactory = ObjectFactory.newInstance();
		stream = StreamFactory.createStream(objectFactory, el, parameters);
		return stream;
	}

	/**
	 * @see backtype.storm.spout.ISpout#open(java.util.Map,
	 *      backtype.storm.task.TopologyContext,
	 *      backtype.storm.spout.SpoutOutputCollector)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void open(Map conf, TopologyContext context,
			SpoutOutputCollector collector) {
		this.output = collector;
		try {

			if (stream == null)
				stream = createStream();

			stream.init();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to open stream: "
					+ e.getMessage());
		}
	}

	/**
	 * @see backtype.storm.spout.ISpout#nextTuple()
	 */
	@Override
	public void nextTuple() {
		log.debug("nextTuple() called");
		try {
			Data item = stream.read();
			log.debug("read item: {}", item);
			if (item == null) {
				sleep(500);
			} else {
				log.debug("Emitting item as tuple...");
				output.emit(new Values(item));
			}
		} catch (Exception e) {
			log.error("Failed to read next item: {}", e.getMessage());
			if (log.isDebugEnabled())
				e.printStackTrace();
		}
	}

	protected void sleep(long ms) {
		try {
			Thread.sleep(ms);
		} catch (Exception e) {
		}
	}

	/**
	 * @see backtype.storm.topology.IComponent#declareOutputFields(backtype.storm
	 *      .topology.OutputFieldsDeclarer)
	 */
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		log.debug("Declaring output-field 'stream.Data'");
		declarer.declare(new Fields("stream.Data"));
	}
}