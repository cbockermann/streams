/**
 * 
 */
package stream.monitor;

import java.util.concurrent.atomic.AtomicLong;

import stream.ProcessorList;
import stream.annotations.Description;
import stream.data.Data;
import stream.data.Statistics;
import stream.statistics.StatisticsService;

/**
 * @author chris
 * 
 */
@Description(group = "Data Stream.Performance", text = "Measures the time per item of inner processors")
public class AverageThroughput extends ProcessorList implements
		StatisticsService {

	AtomicLong nanoTime = new AtomicLong(0L);
	AtomicLong itemsProcessed = new AtomicLong(0L);

	/**
	 * @see stream.ProcessorList#process(stream.data.Data)
	 */
	@Override
	public Data process(Data input) {
		long start = System.currentTimeMillis();
		Data processed = super.process(input);
		long duration = System.currentTimeMillis() - start;
		nanoTime.addAndGet(duration);
		itemsProcessed.incrementAndGet();
		return processed;
	}

	/**
	 * @see stream.service.Service#reset()
	 */
	@Override
	public void reset() throws Exception {
		nanoTime.set(0L);
		itemsProcessed.set(0L);
	}

	/**
	 * @see stream.statistics.StatisticsService#getStatistics()
	 */
	@Override
	public Statistics getStatistics() {
		Statistics stats = new Statistics();
		Double time = nanoTime.doubleValue();
		Double items = itemsProcessed.doubleValue();

		/*
		 * Double rate = 0.0; if (time > 0.0d) { rate = items / time / 1000L; }
		 */

		stats.setName("Average Throughput");
		stats.put("@avg:milliseconds-per-item", time / items);
		return stats;
	}
}