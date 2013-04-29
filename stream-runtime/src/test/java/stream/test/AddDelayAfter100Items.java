/**
 * 
 */
package stream.test;

import stream.AbstractProcessor;
import stream.Data;
import stream.util.Time;

/**
 * @author chris
 * 
 */
public class AddDelayAfter100Items extends AbstractProcessor {

	long last = 0L;
	Time delay = new Time(1000L);
	int count = 0;

	/**
	 * @see stream.Processor#process(stream.Data)
	 */
	@Override
	public Data process(Data input) {
		count++;
		if (count >= 100) {
			sleep(1000L);
		}
		long now = System.currentTimeMillis();
		input.put("@delay", now - last);
		last = now;
		return input;
	}

	protected void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
