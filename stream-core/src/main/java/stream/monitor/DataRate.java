package stream.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.AbstractProcessor;
import stream.data.Data;
import stream.data.Statistics;
import stream.statistics.StatisticsService;

public class DataRate extends AbstractProcessor implements StatisticsService {

	static Logger log = LoggerFactory.getLogger(DataRate.class);
	String clock = null;
	Long count = 0L;
	Long start = null;

	Long windowCount = 0L;
	Long last = 0L;
	Double elapsed = 0.0d;
	Double rate = new Double(0.0);

	Integer every = null;
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

		if (start == null)
			start = System.currentTimeMillis();
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
			rate = windowCount / seconds;
			// log.debug("data rate: {}  (overall: {})", rate, count / elapsed);
			last = now;
			windowCount = 1L;

			if (key != null) {
				input.put("time", new Double(elapsed));
				input.put(key, new Double(rate));
			}

		} else {
			windowCount++;
		}

		if (every != null && count % every.intValue() == 0 && start < now) {
			Long sec = (now - start) / 1000;
			if (sec > 0)
				log.info("{} items processed, data-rate is: {}/second", count,
						count.doubleValue() / sec.doubleValue());
		}

		count++;
		return input;
	}

	@Override
	public void reset() throws Exception {
		count = 0L;
		windowCount = 1L;
		last = 0L;
	}

	@Override
	public Statistics getStatistics() {
		Statistics st = new Statistics();
		st.put("dataRate", rate);
		return st;
	}

	/**
	 * @return the every
	 */
	public Integer getEvery() {
		return every;
	}

	/**
	 * @param every
	 *            the every to set
	 */
	public void setEvery(Integer every) {
		this.every = every;
	}
}