/**
 * 
 */
package stream.monitor;

import java.util.concurrent.atomic.AtomicLong;

import stream.Processor;
import stream.data.Data;
import stream.data.Statistics;
import stream.statistics.StatisticsService;

/**
 * <p>
 * A simple processor that provides statistics about the current Java VM memory
 * usage.
 * </p>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public class MemoryUsage implements Processor, StatisticsService {

	String key = "@jvm:memory";
	AtomicLong memoryUsed = new AtomicLong(0L);

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
	 * @see stream.service.Service#reset()
	 */
	@Override
	public void reset() throws Exception {
		memoryUsed.set(0L);
	}

	/**
	 * @see stream.statistics.StatisticsService#getStatistics()
	 */
	@Override
	public Statistics getStatistics() {
		Statistics stats = new Statistics();
		stats.put(key, memoryUsed.doubleValue());
		return stats;
	}

	/**
	 * @see stream.Processor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data input) {
		Runtime rt = Runtime.getRuntime();
		Long usedMB = (rt.totalMemory() - rt.freeMemory());
		memoryUsed.set(usedMB);
		input.put(key, usedMB);
		return input;
	}
}
