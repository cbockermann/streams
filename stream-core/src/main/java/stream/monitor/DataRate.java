package stream.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.AbstractProcessor;
import stream.data.Data;

public class DataRate extends AbstractProcessor {

	static Logger log = LoggerFactory.getLogger(DataRate.class);
	String clock = null;
	Long count = 0L;

	Long windowCount = 0L;
	Long last = 0L;
	Double elapsed = 0.0d;

	String key = "dataRate";

	public String getClock() {
		return clock;
	}

	public void setClock(String clock) {
		this.clock = clock;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key
	 *            the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public Data process(Data input) {

		Long now = System.currentTimeMillis();

		if (clock != null) {
			now = new Long(input.get(clock) + "");
			if (last == 0L)
				last = now;
			// log.info( "Timestamp: {}, last: {}", now, last );
		}

		Double seconds = Math.abs(last - now) / 1000.0d;
		if (now > last) {
			elapsed += seconds;
			log.info("data rate: {}  (overall: {})", windowCount / seconds,
					count / elapsed);
			last = now;
			windowCount = 1L;

			if (key != null) {
				input.put("time", new Double(elapsed));
				input.put(key, new Double(windowCount / seconds));
			}

		} else {
			windowCount++;
		}

		count++;
		return input;
	}
}