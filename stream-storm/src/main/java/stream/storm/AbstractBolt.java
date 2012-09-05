/**
 * 
 */
package stream.storm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.task.OutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;

/**
 * @author chris
 * 
 */
public abstract class AbstractBolt extends BaseRichBolt {

	/** The unique class ID */
	private static final long serialVersionUID = 5805945428106147592L;
	protected static Logger log = LoggerFactory.getLogger(AbstractBolt.class);

	protected OutputCollector output;
	protected final String xmlConfig;
	protected final String uuid;

	/**
	 * 
	 */
	public AbstractBolt(String xmlConfig, String uuid) {
		this.xmlConfig = xmlConfig;
		this.uuid = uuid;
	}

	/**
	 * @see backtype.storm.topology.IComponent#declareOutputFields(backtype.storm.topology.OutputFieldsDeclarer)
	 */
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		log.debug("Declaring Bolt-output field 'stream.Data'");
		declarer.declare(new Fields("stream.Data"));
	}

}