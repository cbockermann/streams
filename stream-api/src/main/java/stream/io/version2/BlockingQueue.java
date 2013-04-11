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
package stream.io.version2;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

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
public class BlockingQueue extends stream.io.BlockingQueue {

	/**
	 * Linked list node class
	 */
	static class Node<E> {
		E item;

		/**
		 * One of: - the real successor Node - this Node, meaning the successor
		 * is head.next - null, meaning there is no successor (this is the last
		 * node)
		 */
		Node<E> next;

		Node(E x) {
			item = x;
		}
	}

	static Logger log = LoggerFactory.getLogger(BlockingQueue.class);

	protected AtomicBoolean closed = new AtomicBoolean(false);

	/** The capacity bound, or Integer.MAX_VALUE if none */
	private final int capacity;

	/** Current number of elements */
	private final AtomicInteger count = new AtomicInteger(0);

	/**
	 * Head of linked list. Invariant: head.item == null
	 */
	private transient Node<Data> head;

	/**
	 * Tail of linked list. Invariant: last.next == null
	 */
	private transient Node<Data> last;

	/** Lock held by take, poll, etc */
	private final ReentrantLock takeLock = new ReentrantLock();

	/** Wait queue for waiting takes */
	private final Condition notEmpty = takeLock.newCondition();

	/** Lock held by put, offer, etc */
	private final ReentrantLock putLock = new ReentrantLock();

	/** Wait queue for waiting puts */
	private final Condition notFull = putLock.newCondition();

	/**
	 * Creates a {@code LinkedBlockingQueue} with a capacity of
	 * {@link Integer#MAX_VALUE}.
	 */
	public BlockingQueue() {
		this(Integer.MAX_VALUE);
	}

	/**
	 * Creates a {@code LinkedBlockingQueue} with the given (fixed) capacity.
	 * 
	 * @param capacity
	 *            the capacity of this queue
	 * @throws IllegalArgumentException
	 *             if {@code capacity} is not greater than zero
	 */
	public BlockingQueue(int capacity) {
		if (capacity <= 0)
			throw new IllegalArgumentException();
		this.capacity = capacity;
		last = head = new Node<Data>(null);
	}

	private void signalNotEmpty() {
		final ReentrantLock takeLock = this.takeLock;
		takeLock.lock();
		try {
			notEmpty.signal();
		} finally {
			takeLock.unlock();
		}
	}

	/**
	 * Signals a waiting put. Called only from take/poll.
	 */
	private void signalNotFull() {
		final ReentrantLock putLock = this.putLock;
		putLock.lock();
		try {
			notFull.signal();
		} finally {
			putLock.unlock();
		}
	}

	/**
	 * Links node at end of queue.
	 * 
	 * @param node
	 *            the node
	 */
	private void enqueue(Node<Data> node) {
		// assert putLock.isHeldByCurrentThread();
		// assert last.next == null;
		last = last.next = node;
	}

	/**
	 * Removes a node from head of queue.
	 * 
	 * @return the node
	 */
	private Data dequeue() {
		// assert takeLock.isHeldByCurrentThread();
		// assert head.item == null;
		Node<Data> h = head;
		Node<Data> first = h.next;
		h.next = h; // help GC
		head = first;
		Data x = first.item;
		first.item = null;
		return x;
	}

	/**
	 * Lock to prevent both puts and takes.
	 */
	void fullyLock() {
		putLock.lock();
		takeLock.lock();
	}

	/**
	 * Unlock to allow both puts and takes.
	 */
	void fullyUnlock() {
		takeLock.unlock();
		putLock.unlock();
	}

	public int size() {
		return count.get();
	}

	public int remainingCapacity() {
		return capacity - count.get();
	}

	/**
	 * @see stream.io.QueueService#enqueue(stream.Data)
	 */
	public boolean enqueue(Data item) {
		log.debug("Queue {}: Enqueuing event {}", getId(), item);
		try {
			if (item == null)
				return false;

			if (item == Data.END_OF_STREAM)
				return true;
			if (closed.get())
				return false;

			int c = -1;
			Node<Data> node = new Node<Data>(item);
			final ReentrantLock putLock = this.putLock;
			final AtomicInteger count = this.count;
			putLock.lockInterruptibly();
			try {
				while (count.get() == capacity) {
					notFull.await();
				}
				if (closed.get())
					return false;
				enqueue(node);
				c = count.getAndIncrement();
				if (c + 1 < capacity)
					notFull.signal();
			} finally {
				putLock.unlock();
			}
			if (c == 0)
				signalNotEmpty();
			return true;
		} catch (Exception e) {
			log.error("Error enqueuing item: {}", e.getMessage());
			if (log.isDebugEnabled())
				e.printStackTrace();
			return false;
		}
	}

	/**
	 * @see stream.io.Source#init()
	 */
	@Override
	public void init() throws Exception {
		if (getLimit() < 1) {
			throw new IllegalArgumentException("Invalid queue-size '"
					+ getLimit() + "'!");
		}

	}

	/**
	 * @see stream.io.Stream#close()
	 */
	public void close() throws Exception {
		log.debug("Closing queue '{}'...", getId());
		fullyLock();
		try {
			if (closed.get()) {
				log.debug("Queue '{}' already closed.", getId());
				return;
			}
			// log.debug("queue: {}", queue);
			closed.getAndSet(true);

		} finally {
			fullyUnlock();
		}

	}

	/**
	 * @see stream.io.AbstractStream#readItem(stream.Data)
	 */
	@Override
	public Data read() throws Exception {
		log.debug("Reading from queue {}", getId());
		Data item = null;
		int c = -1;
		final AtomicInteger count = this.count;
		final ReentrantLock takeLock = this.takeLock;
		takeLock.lockInterruptibly();
		try {
			if (closed.get() && count.get() == 0) {
				log.debug("Queue '{}' is closed and empty => null", getId());
				return null;
			}
			while (count.get() == 0) {
				notEmpty.await();
			}
			item = dequeue();
			c = count.getAndDecrement();
			log.debug("took item from queue: {}", item);

			if (c > 1)
				notEmpty.signal();

		} catch (InterruptedException e) {
			if (closed.get() && count.get() == 0) {
				log.debug("Queue '{}' is closed and empty => null", getId());
				return null;
			} else {
				log.error("Interruped while waiting for data: {}",
						e.getMessage());
				if (log.isDebugEnabled())
					e.printStackTrace();
			}
		}

		// if (closed) {
		// log.debug("Reading from closed queue '{}'!", getId());
		// return null;
		// }
		finally {
			takeLock.unlock();
		}
		if (c == capacity)
			signalNotFull();
		return item;
	}

	/**
	 * @see stream.io.QueueService#poll()
	 */
	public Data poll() {
		throw new IllegalAccessError("Not Implemented");
	}

	public Data take() {
		throw new IllegalAccessError("Not Implemented");
	}

	/**
	 * @see stream.io.Sink#write(stream.Data)
	 */
	@Override
	public void write(Data item) throws Exception {

		enqueue(item);
	}

	/**
	 * @see stream.io.QueueService#level()
	 */
	@Override
	public int level() {
		return count.get();
	}

	/**
	 * @see stream.io.QueueService#capacity()
	 */
	@Override
	public int capacity() {
		return capacity;
	}

	/**
	 * @see stream.service.Service#reset()
	 */
	@Override
	public void reset() throws Exception {
		fullyLock();
		try {
			for (Node<Data> p, h = head; (p = h.next) != null; h = p) {
				h.next = h;
				p.item = null;
			}
			head = last;
			// assert head.item == null && head.next == null;
			if (count.getAndSet(0) == capacity)
				notFull.signal();
		} finally {
			fullyUnlock();
		}
	}
}