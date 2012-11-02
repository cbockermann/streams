/**
 * 
 */
package stream.storm;

import java.io.ByteArrayInputStream;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import stream.Data;
import stream.StormRunner;
import stream.io.DataStream;
import stream.runtime.setup.DataStreamFactory;
import stream.runtime.setup.ObjectFactory;
import stream.runtime.setup.ProcessorFactory;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

/**
 * @author chris
 * 
 */
public class StreamSpout extends BaseRichSpout {

	/** The unique class ID */
	private static final long serialVersionUID = -786482575770711600L;

	static Logger log = LoggerFactory.getLogger(StreamSpout.class);

	transient DataStream stream;
	protected SpoutOutputCollector output;
	protected final String xmlConfig;
	protected final String uuid;

	public StreamSpout(String xmlConfig, String uuid) {
		log.debug("Creating spout for stream {}", uuid);
		this.xmlConfig = xmlConfig;
		this.uuid = uuid;
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

			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			Document config = builder.parse(new ByteArrayInputStream(xmlConfig
					.getBytes()));

			Element element = StormRunner.findElementByUUID(
					config.getDocumentElement(), uuid);

			if (element == null) {
				throw new Exception("Damn! You screwed the XML!!");
			}

			ObjectFactory obf = ObjectFactory.newInstance();
			ProcessorFactory pf = new ProcessorFactory(obf);
			log.debug("Creating stream from element {}", element);
			stream = DataStreamFactory.createStream(obf, pf, element);
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
			Data item = stream.readNext();
			log.debug("read item: {}", item);
			if (item != null) {
				log.debug("Emitting item as tuple...");
				output.emit(new Values(item));
			}
		} catch (Exception e) {
			log.error("Failed to read next item: {}", e.getMessage());
			if (log.isDebugEnabled())
				e.printStackTrace();
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