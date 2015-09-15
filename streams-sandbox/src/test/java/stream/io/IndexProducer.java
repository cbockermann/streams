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
package stream.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.data.DataFactory;
import cern.jet.random.engine.MersenneTwister64;

public class IndexProducer implements Runnable {
	private final Logger log = LoggerFactory.getLogger(BlockingQueue.class);

	private Join queue;
	private String id;
	private MersenneTwister64 random;
	private long i;
	private int n;

	public IndexProducer(Join queue, String id, int n, int seed) {
		this.queue = queue;
		this.id = id;
		this.i = 0;
		this.n = n;
		random = new MersenneTwister64(seed);
	}

	@Override
	public void run() {
		int c = 0;
		boolean run = true;
		while (run) {
			try {
				i++;
				if (random.nextDouble() < 0.01d) {
					c++;
					Data d = DataFactory.create();
					d.put("id", id);
					d.put("index", i);
					queue.write(d);
					if (c > n) {
						run = false;
						log.info("{}:Finished to write {} elements. ", id, n);
					}
				}

			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}
}
