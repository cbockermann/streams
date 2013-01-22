/**
 * 
 */
package stream.statistics;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.ConditionedProcessor;
import stream.Data;
import stream.ProcessContext;
import stream.data.Statistics;
import stream.expressions.ExpressionResolver;
import stream.util.parser.TimeParser;

/**
 * @author chris
 * 
 */
public class Count extends ConditionedProcessor implements StatisticsService {

	static Logger log = LoggerFactory.getLogger(Count.class);
	String groupBy = null;
	String timeKey = null;
	String prefix = null;

	String window = "5 minutes";
	Long timeInterval = 300 * 1000L;
	String history = "1 day";
	Long timeWindow = timeInterval * 12 * 24;
	String timeFormat = null;

	History<Statistics> historyStats;
	String file = null;
	String separator = ",";
	Long lastTime = 0L;

	Statistics currentStatistics = new Statistics();

	/**
	 * @see stream.Processor#process(stream.Data)
	 */
	@Override
	public Data processMatchingData(Data input) {

		if (groupBy == null)
			return input;

		try {
			String pivot = ExpressionResolver.expand(groupBy, context, input);

			Long time = System.currentTimeMillis();

			if (timeKey != null && input.containsKey(timeKey))
				time = (new Long(input.get(timeKey) + ""));

			log.trace("time = {}", time);
			time = historyStats.mapTimestamp(time);
			log.debug("mapped time is: {} ({})", new Date(time), time);

			Statistics st = historyStats.get(time);
			if (st == null) {
				st = new Statistics();
				st.put("@time", time.doubleValue());
			}
			if (prefix != null) {
				st.add(prefix + pivot, 1.0d);
			} else {
				st.add(pivot, 1.0d);
			}
			historyStats.add(time, st);

			synchronized (currentStatistics) {
				currentStatistics.add(st);
			}

			if (lastTime == 0L)
				lastTime = time;

			if (time != lastTime) {
				log.debug("new time-index, putting out all statistics {}", st);
				input.putAll(st);
				synchronized (currentStatistics) {
					for (String key : currentStatistics.keySet())
						currentStatistics.put(key, new Double(0.0));
				}
				lastTime = time;
			} else {
				log.debug(" lastTime != time  ( {} != {} )", lastTime, time);
			}

			return input;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @see stream.AbstractProcessor#init(stream.ProcessContext)
	 */
	@Override
	public void init(ProcessContext ctx) throws Exception {
		super.init(ctx);
		timeWindow = TimeParser.parseTime(history);
		timeInterval = TimeParser.parseTime(window);

		// long steps = Math.max(1, timeWindow / timeInterval);
		log.debug(
				"Aggregating in intervals of {} ms, keeping {} ms of history",
				timeInterval, timeWindow);
		historyStats = new History<Statistics>(timeInterval, timeWindow);

		if (timeFormat != null) {
			new SimpleDateFormat(timeFormat);
		}
	}

	/**
	 * @return the channelKey
	 */
	public String getGroupBy() {
		return groupBy;
	}

	/**
	 * @param channelKey
	 *            the channelKey to set
	 */
	public void setGroupBy(String channelKey) {
		this.groupBy = channelKey;
	}

	/**
	 * @return the timeKey
	 */
	public String getTimeKey() {
		return timeKey;
	}

	/**
	 * @param timeKey
	 *            the timeKey to set
	 */
	public void setTimeKey(String timeKey) {
		this.timeKey = timeKey;
	}

	/**
	 * @return the window
	 */
	public String getHistory() {
		return history;
	}

	/**
	 * @param window
	 *            the window to set
	 */
	public void setHistory(String window) {
		this.history = window;
	}

	/**
	 * @return the interval
	 */
	public String getWindow() {
		return window;
	}

	/**
	 * @param interval
	 *            the interval to set
	 */
	public void setWindow(String interval) {
		this.window = interval;
	}

	/**
	 * @return the prefix
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * @param prefix
	 *            the prefix to set
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	/**
	 * @return the file
	 */
	public String getFile() {
		return file;
	}

	/**
	 * @param file
	 *            the file to set
	 */
	public void setFile(String file) {
		this.file = file;
	}

	/**
	 * @see stream.service.Service#reset()
	 */
	@Override
	public void reset() throws Exception {
		synchronized (currentStatistics) {
			currentStatistics.clear();
		}
	}

	/**
	 * @see stream.statistics.StatisticsService#getStatistics()
	 */
	@Override
	public Statistics getStatistics() {
		synchronized (currentStatistics) {
			log.debug("Returning copy of statistics {}", currentStatistics);
			Long time = this.historyStats.mapTimestamp(System
					.currentTimeMillis());
			Statistics st = historyStats.get(time);
			if (st != null)
				return new Statistics(historyStats.get(time));
			return new Statistics();
		}
	}
}
