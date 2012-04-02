/**
 * 
 */
package stream.logic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.ConditionedDataProcessor;
import stream.data.Data;
import stream.runtime.Context;
import stream.util.Description;
import stream.util.SimpleTimeParser;

/**
 * A simple processor that artificially delays the data processing by a
 * specified amount of time. Useful for watching high-speed data streams being
 * processed with visual components attached.
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
@Description(group = "Data Stream.Logic")
public class Delay extends ConditionedDataProcessor {

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
	public void init(Context ctx) throws Exception {
		super.init(ctx);
		if (time != null && !"".equals(time.trim()))
			milliseconds = SimpleTimeParser.parseTime(time);
		else
			milliseconds = 0L;
	}

	/**
	 * @see stream.data.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data processMatchingData(Data data) {
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