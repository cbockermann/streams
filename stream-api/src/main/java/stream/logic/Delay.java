/**
 * 
 */
package stream.logic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.AbstractDataProcessor;
import stream.data.Data;
import stream.util.SimpleTimeParser;

/**
 * @author chris
 * 
 */
public class Delay extends AbstractDataProcessor {

	static Logger log = LoggerFactory.getLogger(Delay.class);

	Long milliseconds = 0L;
	String time = "";

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
	 * @see stream.data.AbstractDataProcessor#init()
	 */
	@Override
	public void init() throws Exception {
		super.init();
		if (time != null && !"".equals(time.trim()))
			milliseconds = SimpleTimeParser.parseTime(time);
		else
			milliseconds = 0L;
	}

	/**
	 * @see stream.data.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data data) {
		if (milliseconds > 0) {
			try {
				Thread.sleep(milliseconds);
			} catch (Exception e) {
				log.error("Failed to delay execution: {}", e.getMessage());
			}
		}

		return data;
	}
}
