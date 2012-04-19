/**
 * 
 */
package stream.flow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.ProcessContext;
import stream.StatefulProcessor;
import stream.data.Data;
import stream.util.parser.TimeParser;

/**
 * @author chris
 * 
 */
public class Delay implements StatefulProcessor {

	static Logger log = LoggerFactory.getLogger(Delay.class);
	String time;
	Long sleep = null;

	/**
	 * @return the time
	 */
	public String getTime() {
		return time;
	}

	/**
	 * @param time
	 *            the time to set
	 */
	public void setTime(String time) {
		this.time = time;
	}

	/**
	 * @see stream.Processor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data input) {
		if (sleep != null && sleep > 0) {
			try {
				Thread.sleep(sleep);
			} catch (Exception e) {
			}
		}

		return input;
	}

	/**
	 * @see stream.StatefulProcessor#init(stream.ProcessContext)
	 */
	@Override
	public void init(ProcessContext context) throws Exception {
		sleep = TimeParser.parseTime(time);
		log.info("Delay time is {} ms", sleep);
	}

	/**
	 * @see stream.StatefulProcessor#resetState()
	 */
	@Override
	public void resetState() throws Exception {
		// TODO Auto-generated method stub

	}

	/**
	 * @see stream.StatefulProcessor#finish()
	 */
	@Override
	public void finish() throws Exception {
		// TODO Auto-generated method stub

	}
}
