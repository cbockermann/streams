/**
 * 
 */
package stream.storm;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.DataFactory;
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
public class ClockSpout extends BaseRichSpout {

	/** The unique class ID */
	private static final long serialVersionUID = 812144742121538026L;

	static Logger log = LoggerFactory.getLogger(ClockSpout.class);

	Long interval = 1000L;
	SpoutOutputCollector output;

	public ClockSpout(Long interval) {
		this.interval = interval;
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
	}

	/**
	 * @see backtype.storm.spout.ISpout#nextTuple()
	 */
	@Override
	public void nextTuple() {
		try {
			Thread.sleep(interval);
		} catch (Exception e) {
			log.error("Error while taking my interval-nap: {}", e.getMessage());
			if (log.isDebugEnabled())
				e.printStackTrace();
		}

		Data item = DataFactory.create();
		item.put("@time", System.currentTimeMillis());
		output.emit(new Values(item));
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
