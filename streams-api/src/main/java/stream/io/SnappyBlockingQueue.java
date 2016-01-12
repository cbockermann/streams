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
			.getLogger(SnappyBlockingQueue.class);

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

	protected boolean writeSnap = false;
	protected boolean readSnap = false;

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

	protected boolean conditionWriteSnap() {
		// Thread is snapped and the queue is > 1/3 full
		return (writeSnap && count * 3 > capacity)
		// Thread is not snapped and the queue is full
				|| (writeSnap == false && count == capacity);

	}

	protected boolean conditionReadNotSnap() {
		return readSnap && (count * 3) / 2 > capacity;
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
				readSnap = true;
				notEmpty.await();
			}
			return extract();

			// } catch (InterruptedException e) {
			// if (closed && count == 0) {
			// log.debug("Queue '{}' is closed and empty => null", getId());
			// return null;
			// } else {
			// log.error("Interruped while waiting for data: {}",
			// e.getMessage());
			// if (log.isDebugEnabled())
			// e.printStackTrace();
			// }
		} finally {
			lock.unlock();
		}
		// return null;
	}

	private Data extract() {
		final Data[] data = this.data;
		Data item = data[last];
		data[last] = null;
		last = inc(last);
		--count;
		log.trace("last: {}", last);

		log.trace("take size: {}", count);
		log.trace("took item from queue: {}", item);

		if (!writeSnap)
			notFull.signal();
		else if (conditionWriteSnap()) {
			writeSnap = false;
			notFull.signal();
			// log.info("changed state");
		}
		return item;
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
			while (conditionWriteSnap()) {
				writeSnap = true;
				notFull.await();
			}
			return insert(item);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * @see stream.io.QueueService#enqueue(stream.Data)
	 */
	private boolean insert(Data item) {
		// insert dataItem
		data[head] = item;
		head = inc(head);
		++count;
		//
		if (!readSnap)
			notEmpty.signal();
		else if (conditionReadNotSnap()) {
			readSnap = false;
			notEmpty.signal();
		}
		return true;
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

	// ############ QueueService

	@Override
	public Data poll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Data take() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean enqueue(Data item) {
		// TODO Auto-generated method stub
		return false;
	}

}