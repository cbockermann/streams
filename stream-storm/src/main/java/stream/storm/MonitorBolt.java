/**
 * 
 */
package stream.storm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chris
 * 
 */
public class MonitorBolt extends ProcessBolt {

	/** The unique class ID */
	private static final long serialVersionUID = -924312414467186051L;

	static Logger log = LoggerFactory.getLogger(MonitorBolt.class);

	public MonitorBolt(String xmlConfig, String uuid) {
		super(xmlConfig, uuid);
	}
}