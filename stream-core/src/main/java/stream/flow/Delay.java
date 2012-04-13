/**
 * 
 */
package stream.flow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.ConditionedProcessor;
import stream.Context;
import stream.annotations.Description;
import stream.annotations.Parameter;
import stream.data.Data;
import stream.util.parser.TimeParser;

/**
 * A simple processor that artificially delays the data processing by a
 * specified amount of time. Useful for watching high-speed data streams being
 * processed with visual components attached.
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
@Description(group = "Data Stream.Logic")
public class Delay extends ConditionedProcessor {

	static Logger log = LoggerFactory.getLogger(Delay.class);

	Long milliseconds = 0L;
	String time = "1000ms";

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
	@Parameter(defaultValue = "1000ms")
	public void setTime(String time) {
		this.time = time;
	}

	/**
	 * @see stream.AbstractProcessor#init()
	 */
	@Override
	public void init(Context ctx) throws Exception {
		super.init(ctx);
		if (time != null && !"".equals(time.trim()))
			milliseconds = TimeParser.parseTime(time);
		else
			milliseconds = 0L;
	}

	/**
	 * @see stream.DataProcessor#process(stream.data.Data)
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