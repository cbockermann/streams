/*
 *  stream.ai
 *
 *  Copyright (C) 2011-2012 by Christian Bockermann, Hendrik Blom
 * 
 *  stream.ai is a library, API and runtime environment for processing high
 *  volume data streams. It is composed of three submodules "stream-api",
 *  "stream-core" and "stream-runtime".
 *
 *  The stream.ai library (and its submodules) is free software: you can 
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
package stream.runtime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Statistics;

/**
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public class MemoryLogger extends Thread {

	static Logger log = LoggerFactory.getLogger(MemoryLogger.class);

	public MemoryLogger() {
	}

	public void run() {
		while (true) {
			Long mem0 = Runtime.getRuntime().totalMemory()
					- Runtime.getRuntime().freeMemory();
			log.info("Current memory usage is {} bytes", mem0);

			try {
				Statistics stats = new Statistics();
				stats.put("memory.usage", mem0.doubleValue());
				log.info("Memory usage: {}", stats);
			} catch (Exception e) {
				if (log.isTraceEnabled())
					e.printStackTrace();
			}

			try {
				Thread.sleep(5 * 1000L);
			} catch (Exception e) {
			}
		}
	}
}