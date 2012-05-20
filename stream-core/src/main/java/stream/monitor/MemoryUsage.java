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

	AtomicLong memoryUsed = new AtomicLong(0L);

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
		stats.put("@memory:jvm", memoryUsed.doubleValue());
		return stats;
	}

	/**
	 * @see stream.Processor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data input) {
		System.gc();
		Runtime rt = Runtime.getRuntime();
		long usedMB = (rt.totalMemory() - rt.freeMemory());
		memoryUsed.set(usedMB);
		return input;
	}
}
