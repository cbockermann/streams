/**
 * 
 */
package stream.storm;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.io.Stream;
import stream.runtime.Variables;
import stream.runtime.setup.StreamFactory;
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

	public StreamSpout(String className, Map<String, String> params) {
		System.out.println("Beginning of weirdness!");
		log.debug("Creating spout for stream (class: {}, params: {})",
				className, params);
		this.className = className;
		this.parameters = new Variables(params);
		System.out.println("Totally weird!");
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
		System.out.println("Weird!?");
		this.output = collector;
		try {
			Map<String, String> params = new HashMap<String, String>(parameters);
			log.info("Creating stream for class: {}, params: {}", className,
					params);
			stream = StreamFactory.createStream(className, params);
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