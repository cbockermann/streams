/*
 *  streams library
 *
 *  Copyright (C) 2011-2014 by Christian Bockermann, Hendrik Blom
 * 
 *  streams is a library, API and runtime environment for processing high
 *  volume data streams. It is composed of three submodules "stream-api",
 *  "stream-core" and "stream-runtime".
 *
 *  The streams library (and its submodules) is free software: you can 
 *  redistribute it and/or modify it under the terms of the 
 *  GNU Affero General Public License as published by the Free Software 
 *  Foundation, either version 3 of the License, or (at your option) any 
 *  later version.
 *
 *  The stream.ai library (and its submodules) is distributed in the hope
 *  that it will be useful, but WITHOUT ANY WARRANTY; without even the implied 
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/.
 */
package stream.monitor;

import java.util.concurrent.atomic.AtomicLong;

import stream.Data;
import stream.ProcessorList;
import stream.annotations.Description;
import stream.data.Statistics;
import stream.statistics.StatisticsService;

/**
 * @author chris
 * 
 */
@Description(group = "Streams.Monitoring", text = "Measures the time per item of inner processors")
public class AverageThroughput extends ProcessorList implements
		StatisticsService {

	AtomicLong nanoTime = new AtomicLong(0L);
	AtomicLong itemsProcessed = new AtomicLong(0L);

	/**
	 * @see stream.ProcessorList#process(stream.Data)
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