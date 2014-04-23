package stream.monitor;

import java.text.DecimalFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.AbstractProcessor;
import stream.Data;
import stream.ProcessContext;
import stream.data.Statistics;
import stream.statistics.StatisticsService;

public class DataRate extends AbstractProcessor implements StatisticsService {

	final DecimalFormat fmt = new DecimalFormat("0.000");
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
	String id;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

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

	/**
	 * @see stream.AbstractProcessor#init(stream.ProcessContext)
	 */
	@Override
	public void init(ProcessContext ctx) throws Exception {
		super.init(ctx);
		// start = System.currentTimeMillis();
	}

	@Override
	public Data process(Data input) {

		if (start == null)
			start = System.currentTimeMillis();
		// Long now = System.currentTimeMillis();
		//
		// if (clock != null) {
		// now = new Long(input.get(clock) + "");
		// if (last == 0L)
		// last = now;
		// // log.info( "Timestamp: {}, last: {}", now, last );
		// }
		//
		// Double seconds = Math.abs(last - now) / 1000.0d;
		// if (now > last) {
		// elapsed += seconds;
		// rate = windowCount / seconds;
		// // log.debug("data rate: {}  (overall: {})", rate, count / elapsed);
		// last = now;
		// windowCount = 1L;
		//
		// if (key != null) {
		// input.put("time", new Double(elapsed));
		// input.put(key, new Double(rate));
		// }
		//
		// } else {
		// windowCount++;
		// }

		count++;
		if (every != null && count % every.intValue() == 0) {
			printDataRate(System.currentTimeMillis());
		}

		Long t = System.currentTimeMillis() - start;
		if (t > 0 && count % 10 == 0) {
			synchronized (rate) {
				rate = this.count.doubleValue() / (t.doubleValue() / 1000.0d);
			}
		}

		return input;
	}

	public void printDataRate() {
		printDataRate(System.currentTimeMillis());
	}

	protected void printDataRate(Long now) {
		Long sec = (now - start) / 1000;
		if (sec > 0)
			log.info("Data rate '" + getId()
					+ "': {} items processed, data-rate is: {}/second", count,
					fmt.format(count.doubleValue() / sec.doubleValue()));
	}

	/**
	 * @see stream.AbstractProcessor#finish()
	 */
	@Override
	public void finish() throws Exception {
		super.finish();

		if (start != null) {
			Long now = System.currentTimeMillis();
			Long sec = (now - start);
			log.info("DataRate processor '" + id
					+ "' has been running for {} ms, {} items.", sec,
					count.doubleValue());
			Double s = sec.doubleValue() / 1000.0d;
			if (s > 0)
				log.info(
						"Overall average data-rate for processor '{}' is: {}/second",
						id, fmt.format(count.doubleValue() / s));
		} else {
			log.info("Start time not available.");
		}
	}

	@Override
	public void reset() throws Exception {
		count = 0L;
		windowCount = 1L;
		last = 0L;
		start = null;
	}

	@Override
	public Statistics getStatistics() {
		Statistics st = new Statistics();
		synchronized (rate) {
			st.put("dataRate", new Double(rate.doubleValue()));
		}
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