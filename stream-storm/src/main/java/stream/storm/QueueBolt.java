/**
 * 
 */
package stream.storm;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.tuple.Tuple;

/**
 * @author chris
 * 
 */
public class QueueBolt extends AbstractBolt {

	/** The unique class ID */
	private static final long serialVersionUID = -3206574886699994554L;

	static Logger log = LoggerFactory.getLogger(QueueBolt.class);
	OutputCollector output;

	/**
	 * @param xmlConfig
	 * @param uuid
	 */
	public QueueBolt(String xmlConfig, String uuid) {
		super(xmlConfig, uuid);
	}

	/**
	 * @see backtype.storm.task.IBolt#prepare(java.util.Map,
	 *      backtype.storm.task.TopologyContext,
	 *      backtype.storm.task.OutputCollector)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector) {
		output = collector;
		log.info("  > Preparing queue '{}' with collector '{}'", this.uuid,
				collector);
	}

	/**
	 * @see backtype.storm.task.IBolt#execute(backtype.storm.tuple.Tuple)
	 */
	@Override
	public void execute(Tuple input) {
		log.info("Executing for tuple {}", input);
		if (output != null) {
			log.info("   emitting tuple...");
			output.emit(input.getValues());
		} else {
			log.info("   no output defined, discarding tuple...");
		}
	}
}
