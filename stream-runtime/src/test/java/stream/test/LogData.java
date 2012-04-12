/**
 * 
 */
package stream.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.AbstractDataProcessor;
import stream.data.Data;

/**
 * @author chris
 * 
 */
public class LogData extends AbstractDataProcessor {

	static Logger log = LoggerFactory.getLogger(LogData.class);

	/**
	 * @see stream.data.Processor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data input) {
		log.info("Data: {}", input);
		return input;
	}
}