/**
 * 
 */
package stream.io;

import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;

/**
 * @author chris
 *
 */
public class DefaultQueue extends AbstractQueue {

	static Logger log = LoggerFactory.getLogger(DefaultQueue.class);
	LinkedBlockingQueue<Data> queue = new LinkedBlockingQueue<Data>();
	final AtomicBoolean closed = new AtomicBoolean(false);

	int size = 100000;

	/**
	 * @see stream.io.Queue#getSize()
	 */
	@Override
	public Integer getSize() {
		return queue.size();
	}

	/**
	 * @see stream.io.Barrel#clear()
	 */
	@Override
	public int clear() {
		int sz = queue.size();
		queue.clear();
		return sz;
	}

	/**
	 * @see stream.io.Sink#init()
	 */
	@Override
	public void init() throws Exception {
		log.debug("Creating blocking queue of size {}", size);
		queue = new LinkedBlockingQueue<Data>(size);
	}

	/**
	 * @see stream.io.Sink#write(stream.Data)
	 */
	@Override
	public boolean write(Data item) throws Exception {
		while (!queue.offer(item)) {
			try {
				log.debug("Failed to insert into queue... thread yielding");
				Thread.yield();
			} catch (Exception e) {
			}
		}
		log.debug("item inserted.");
		return true; // queue.add(item);
	}

	/**
	 * @see stream.io.Sink#write(java.util.Collection)
	 */
	@Override
	public boolean write(Collection<Data> data) throws Exception {

		for (Data item : data) {
			boolean ret = write(item);
			if (!ret) {
				return false;
			}
		}

		return true;
	}

	/**
	 * @see stream.io.Sink#close()
	 */
	@Override
	public void close() throws Exception {
		queue.clear();
		closed.set(false);
	}

	/**
	 * @see stream.io.Source#read()
	 */
	@Override
	public Data read() throws Exception {
		while (!closed.get()) {
			try {
				Data item = queue.take();
				if (item != null) {
					return item;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * @see stream.io.QueueService#poll()
	 */
	@Override
	public Data poll() {
		return queue.poll();
	}

	/**
	 * @see stream.io.QueueService#take()
	 */
	@Override
	public Data take() {
		try {
			return read();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @see stream.io.QueueService#enqueue(stream.Data)
	 */
	@Override
	public boolean enqueue(Data item) {
		return queue.add(item);
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
		return queue.remainingCapacity();
	}

	/**
	 * @see stream.service.Service#reset()
	 */
	@Override
	public void reset() throws Exception {
		queue.clear();
	}
}
