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

public class IndexConsumer implements Runnable {
	final Logger log = LoggerFactory.getLogger(BlockingQueue.class);

	private Join join;
	private int reads;
	private java.util.concurrent.BlockingQueue<Boolean> resultQueue;

	public IndexConsumer(Join join, int reads,
			java.util.concurrent.BlockingQueue<Boolean> resultQueue) {
		this.join = join;
		this.reads = reads;
		this.resultQueue = resultQueue;
	}

	@Override
	public void run() {
		int c = 0;
		long index = 0;
		while (c < reads) {
			Data d;
			try {
				d = join.read();
				long tindex = (Long) d.get("index");
				if (tindex < index)
					resultQueue.put(false);
				index = tindex;
				c++;
				// System.out.println(d + ":"+c);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		try {
			resultQueue.put(true);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
