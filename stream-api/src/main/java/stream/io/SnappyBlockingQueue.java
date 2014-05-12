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

import java.util.Collection;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;

/**
 * <p>
 * The snappy implementation of a blocking queue of data items.
 * </p>
 * 
 * @author Hendrik Blom
 * 
 */
public class SnappyBlockingQueue extends AbstractQueue {

	private static final Logger log = LoggerFactory
			.getLogger(SnappyBlockingQueue2.class);

	protected boolean closed = false;

	/** Current number of elements */
	private int count = 0;

	private final Data[] data;

	/** Main lock guarding all access */
	final ReentrantLock lock;
	/** Condition for waiting takes */
	private final Condition notEmpty;
	/** Condition for waiting puts */
	private final Condition notFull;

	private int last = 0;
	private int head = 0;

	private boolean state = false;
	private boolean readState = false;

	/**
	 * Creates a {@code LinkedBlockingQueue} with the given (fixed) capacity.
	 * 
	 * @param capacity
	 *            the capacity of this queue
	 * @throws IllegalArgumentException
	 *             if {@code capacity} is not greater than zero
	 */

	public SnappyBlockingQueue() {
		this(10000);
	}

	public SnappyBlockingQueue(int capacity) {
		super();
		if (capacity <= 0)
			throw new IllegalArgumentException();
		this.capacity = capacity;
		data = new Data[capacity];
		lock = new ReentrantLock();
		notEmpty = lock.newCondition();
		notFull = lock.newCondition();

	}

	public int size() {
		return count;
	}

	public int remainingCapacity() {
		return capacity - count;
	}

	/**
	 * Circularly increment i.
	 */
	final int inc(int i) {
		return (++i == data.length) ? 0 : i;
	}

	/**
	 * Circularly decrement i.
	 */
	final int dec(int i) {
		return ((i == 0) ? data.length : i) - 1;
	}

	/**
	 * @see stream.io.QueueService#enqueue(stream.Data)
	 */
	public boolean enqueue(Data item) {
		try {
			// here is the magic!!!
			while (condition()) {
				state = true;
				notFull.await();
			}
			data[head] = item;
			// log.info("head: {}", head);
			head = inc(head);
			++count;
			// log.info(this.toString() + "==>" + count);
			if (!readState)
				notEmpty.signal();
			else if (readState && count > (2 / 3) * capacity) {
				readState = false;
				notEmpty.signal();
			}

			return true;
		} catch (Exception e) {
			log.error("Error enqueuing item: {}", e.getMessage());
			if (log.isDebugEnabled())
				e.printStackTrace();
			return false;
		}
	}

	public boolean condition() {
		return (state && count * 3 > capacity)
				|| (state == false && count == capacity);

		// return count == capacity;
	}

	/**
	 * @see stream.io.Source#init()
	 */
	@Override
	public void init() throws Exception {
		if (getCapacity() < 1) {
			throw new IllegalArgumentException("Invalid queue-capacity '"
					+ getCapacity() + "'!");
		}
	}

	/**
	 * @see stream.io.Stream#close()
	 */
	public void close() throws Exception {
		log.debug("Closing queue '{}'...", getId());
		lock.lockInterruptibly();
		try {
			if (closed) {
				log.debug("Queue '{}' already closed.", getId());
				return;
			}
			// log.debug("queue: {}", queue);
			closed = true;

		} finally {
			lock.unlock();
		}

	}

	/**
	 * @see stream.io.AbstractStream#readItem(stream.Data)
	 */
	@Override
	public Data read() throws Exception {
		log.trace("Reading from queue {}", getId());
		lock.lockInterruptibly();
		try {
			if (closed && count == 0) {
				log.debug("Queue '{}' is closed and empty => null", getId());
				return null;
			}
			while (count == 0) {
				readState = true;
				notEmpty.await();
			}

			final Data[] data = this.data;
			Data item = data[last];
			data[last] = null;
			last = inc(last);
			--count;
			log.debug("last: {}", last);

			// ////////////////////////
			// TODO Signals checken !!!!!
			// TODO
			log.debug("take size: {}", count);
			log.trace("took item from queue: {}", item);

			notEmpty.signal();
			if (!state)
				notFull.signal();
			else if (state && count * 3 < capacity) {
				state = false;
				notFull.signal();
				// log.info("changed state");
			}
			return item;
		} catch (InterruptedException e) {
			if (closed && count == 0) {
				log.debug("Queue '{}' is closed and empty => null", getId());
				return null;
			} else {
				log.error("Interruped while waiting for data: {}",
						e.getMessage());
				if (log.isDebugEnabled())
					e.printStackTrace();
			}
		} finally {
			lock.unlock();
		}
		return null;
	}

	/**
	 * @see stream.io.QueueService#poll()
	 */
	public Data poll() {
		throw new IllegalAccessError("Not Implemented");
	}

	public Data take() {
		try {
			return read();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @see stream.io.Sink#write(stream.Data)
	 */
	@Override
	public boolean write(Data item) throws Exception {
		log.trace("Queue {}: Enqueuing event {}", getId(), item);

		if (item == null)
			throw new NullPointerException();
		if (closed)
			return false;

		final ReentrantLock lock = this.lock;
		lock.lockInterruptibly();
		try {
			return enqueue(item);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public boolean write(Collection<Data> data) throws Exception {
		throw new IllegalAccessError("Not Implemented");
	}

	/**
	 * @see stream.io.Barrel#clear()
	 */
	@Override
	public int clear() {
		try {
			lock.lockInterruptibly();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		try {
			int removed = count;
			for (int i = 0; i < capacity; i++) {
				data[i] = null;
			}
			head = last = 0;
			count = 0;
			return removed;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * @see stream.io.QueueService#level()
	 */
	@Override
	public int level() {
		return count;
	}

	/**
	 * @see stream.io.QueueService#capacity()
	 */
	@Override
	public int capacity() {
		return capacity;
	}

	@Override
	public Integer getSize() {
		return count;
	}

	/**
	 * @see stream.service.Service#reset()
	 */
	@Override
	public void reset() throws Exception {
	}

	public String toString() {
		return "stream.io.BlockingQueue['" + id + "']";
	}

}