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
package stream.io;

import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;

/**
 * <p>
 * The default implementation of a blocking queue of data items.
 * </p>
 * 
 * @author Hendrik Blom, Christian Bockermann
 * 
 */
public class BlockingQueue extends AbstractQueue {

	static Logger log = LoggerFactory.getLogger(BlockingQueue.class);

	protected boolean closed = false;
	protected LinkedBlockingQueue<Data> queue;

	/**
	 * @see stream.io.Source#init()
	 */
	@Override
	public void init() throws Exception {
		if (getLimit() < 1) {
			throw new IllegalArgumentException("Invalid queue-size '"
					+ getLimit() + "'!");
		}

		queue = new LinkedBlockingQueue<Data>(getLimit());
	}

	/**
	 * @see stream.io.Stream#close()
	 */
	public void close() throws Exception {
		log.debug("Closing queue '{}'...", getId());
		synchronized (queue) {
			if (closed) {
				log.debug("Queue '{}' already closed.", getId());
				return;
			}

			log.debug("queue: {}", queue);
			queue.put(Data.END_OF_STREAM);
			log.debug("queue': {}", queue);
			queue.notifyAll();
			closed = true;
		}
	}

	/**
	 * @see stream.io.AbstractStream#readItem(stream.Data)
	 */
	@Override
	public Data read() throws Exception {
		log.debug("Reading from queue {}", getId());
		Data item = null;
		try {
			if (closed && queue.isEmpty()) {
				log.debug("Queue '{}' is closed and empty => null", getId());
				return null;
			}
			item = queue.take();
			log.debug("took item from queue: {}", item);
		} catch (InterruptedException e) {
			if (closed && queue.isEmpty())
				return null;
			else {
				log.error("Interruped while waiting for data: {}",
						e.getMessage());
				if (log.isDebugEnabled())
					e.printStackTrace();
			}
		}

		if (item == Data.END_OF_STREAM) {
			log.debug("Next data-item is end-of-stream event!");
			return null;
		}

		return item;
	}

	/**
	 * @see stream.io.QueueService#poll()
	 */
	public Data poll() {
		return queue.poll();
	}

	public Data take() {
		try {
			Data item = read();
			return item;
		} catch (Exception e) {
			log.error("Interrupted while reading on queue: {}", e.getMessage());
			if (log.isDebugEnabled())
				e.printStackTrace();
			return null;
		}
	}

	/**
	 * @see stream.io.QueueService#enqueue(stream.Data)
	 */
	public boolean enqueue(Data item) {
		log.debug("Queue {}: Enqueuing event {}", getId(), item);
		try {
			if (item == null) {
				return false;
			}

			if (item == Data.END_OF_STREAM)
				return true;

			synchronized (queue) {
				if (!closed)
					queue.put(item);
				queue.notifyAll();
			}
			return true;
		} catch (Exception e) {
			log.error("Error enqueuing item: {}", e.getMessage());
			if (log.isDebugEnabled())
				e.printStackTrace();
			return false;
		}
	}

	/**
	 * @see stream.io.Sink#write(stream.Data)
	 */
	@Override
	public void write(Data item) throws Exception {

		log.debug("Writing to queue '{}': {}", getId(), item);
		if (item == null) {
			log.debug("'null' must not be written to a queue (queue id: '{}')",
					getId());
			return;
		}

		if (item == Data.END_OF_STREAM)
			return;

		synchronized (queue) {
			if (closed) {
				// queue.notifyAll();
				log.error("Write to closed queue '{}'!", getId());
				// throw new Exception("Queue " + getId() + " already closed.");
			} else {
				log.debug("queue '{}': Adding item '{}'", getId(), item);
				queue.put(item);
				queue.notifyAll();
			}
		}
	}

	/**
	 * @see stream.io.QueueService#level()
	 */
	@Override
	public int level() {
		return queue.size();
	}

	/**
	 * @see stream.io.QueueService#capacity()
	 */
	@Override
	public int capacity() {
		return getLimit();
	}

	/**
	 * @see stream.service.Service#reset()
	 */
	@Override
	public void reset() throws Exception {
		log.debug("Resetting queue '{}'", getId());
		if (queue == null) {
			queue = new LinkedBlockingQueue<Data>(this.getLimit());
		} else {
			queue.clear();
		}
	}
}