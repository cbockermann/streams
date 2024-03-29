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
package stream.io.active;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.io.AbstractStream;
import stream.io.Stream;

/**
 * *
 * <p>
 * A simple active stream implementation.
 * </p>
 * 
 * @author Hendrik Blom
 * 
 */
public class SimpleActiveStream extends AbstractStream implements ActiveStream {

	protected Logger log = LoggerFactory.getLogger(SimpleActiveStream.class);
	protected final LinkedBlockingQueue<Data> queue;

	protected Stream stream;
	protected StreamActivator activator;
	protected ExecutorService pool;

	public SimpleActiveStream(Stream stream, ExecutorService pool) {
		this.stream = stream;
		this.queue = new LinkedBlockingQueue<Data>(100);
		this.pool = pool;
	}

	@Override
	public void activate() throws Exception {
		this.activator = new StreamActivator();
		pool.execute(this.activator);
	}

	/**
	 * @see stream.io.Stream#init()
	 */
	@Override
	public void init() throws Exception {
		stream.init();
	}

	@Override
	public Data readNext() throws Exception {
		return queue.poll();
	}

	
	
	
	@Override
	public void close() throws Exception {
		pool.shutdownNow();
		super.close();
	}




	private class StreamActivator implements Runnable {

		public StreamActivator() {
		}

		public void run() {
			while (true) {
				try {
					queue.put(stream.read());
				} catch (InterruptedException e) {
					log.error("Interrupted while reading stream: {}",
							e.getMessage());
					if (log.isDebugEnabled())
						e.printStackTrace();
				} catch (Exception e) {
					log.error("Error while reading stream: {}", e.getMessage());
					if (log.isDebugEnabled())
						e.printStackTrace();
				}
			}
		}

	}

}
