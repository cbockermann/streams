/*
 *  streams library
 *
 *  Copyright (C) 2011-2012 by Christian Bockermann, Hendrik Blom
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

import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Processor;
import stream.data.Data;
import stream.data.DataFactory;
import stream.io.DataStream;

/**
 * *
 * <p>
 * A simple active stream implementation.
 * </p>
 * 
 * @author Hendrik Blom
 * 
 */
public class ActiveDataStreamImpl implements ActiveDataStream {

	static Logger log = LoggerFactory.getLogger(ActiveDataStreamImpl.class);
	protected final LinkedBlockingQueue<Data> queue;

	protected DataStream stream;
	protected StreamActivator activator;

	public ActiveDataStreamImpl(DataStream stream) {
		this.stream = stream;
		this.queue = new LinkedBlockingQueue<Data>(100);
	}

	@Override
	public Map<String, Class<?>> getAttributes() {
		return stream.getAttributes();
	}

	@Override
	public Data readNext() throws Exception {
		return readNext(DataFactory.create());
	}

	@Override
	public Data readNext(Data datum) throws Exception {

		if (queue.isEmpty())
			return null;

		Data d = queue.poll();
		if (d != null)
			datum.putAll(d);
		return datum;
	}

	@Override
	public void close() throws Exception {
		stream.close();
		this.activator.setRun(false);
	}

	@Override
	public List<Processor> getPreprocessors() {
		return stream.getPreprocessors();
	}

	@Override
	public void activate() throws Exception {
		this.activator = new StreamActivator();
		this.activator.start();
	}

	private class StreamActivator extends Thread {
		private boolean run = true;

		public StreamActivator() {
			run = true;
		}

		public void run() {
			while (run) {
				try {
					queue.put(stream.readNext());
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

		public void setRun(boolean run) {
			this.run = run;
			this.interrupt();
		}
	}

	/**
	 * @see stream.io.DataStream#init()
	 */
	@Override
	public void init() throws Exception {
		stream.init();
	}
}
