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
import stream.Processor;
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
	 * @see stream.Processor#process(stream.Data)
	 */
	@Override
	public Data process(Data input) {
		Runtime rt = Runtime.getRuntime();
		Long free = rt.freeMemory();
		Long usedMB = (rt.totalMemory() - rt.freeMemory());
		memoryUsed.set(usedMB);

		input.put(key, usedMB);
		input.put("@jvm:free", free);
		return input;
	}
}
