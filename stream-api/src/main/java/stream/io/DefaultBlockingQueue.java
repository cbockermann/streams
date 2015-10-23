/**
 * 
 */
package stream.io;

import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import stream.Data;

/**
 * @author chris
 *
 */
public class DefaultBlockingQueue implements Queue {

	String id;
	final AtomicBoolean closed = new AtomicBoolean(false);
	final LinkedBlockingQueue<Data> queue = new LinkedBlockingQueue<Data>();

	/**
	 * @see stream.io.Barrel#clear()
	 */
	@Override
	public int clear() {
		synchronized (queue) {
			int size = queue.size();
			queue.clear();
			return size;
		}
	}

	/**
	 * @see stream.io.Sink#getId()
	 */
	@Override
	public String getId() {
		return id;
	}

	/**
	 * @see stream.io.Sink#setId(java.lang.String)
	 */
	@Override
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @see stream.io.Sink#init()
	 */
	@Override
	public void init() throws Exception {
	}

	/**
	 * @see stream.io.Sink#write(stream.Data)
	 */
	@Override
	public boolean write(Data item) throws Exception {
		if (closed.get()) {
			return false;
		}
		return queue.add(item);
	}

	/**
	 * @see stream.io.Sink#write(java.util.Collection)
	 */
	@Override
	public boolean write(Collection<Data> data) throws Exception {
		if (closed.get()) {
			return false;
		}
		return queue.addAll(data);
	}

	/**
	 * @see stream.io.Sink#close()
	 */
	@Override
	public void close() throws Exception {
		closed.set(true);
	}

	/**
	 * @see stream.io.Source#read()
	 */
	@Override
	public Data read() throws Exception {
		if (closed.get() && queue.isEmpty()) {
			return null;
		}

		return queue.take();
	}

	/**
	 * @see stream.io.Queue#setCapacity(java.lang.Integer)
	 */
	@Override
	public void setCapacity(Integer limit) {

	}

	/**
	 * @see stream.io.Queue#getSize()
	 */
	@Override
	public Integer getSize() {
		return queue.size();
	}

	/**
	 * @see stream.io.Queue#getCapacity()
	 */
	@Override
	public Integer getCapacity() {
		return Integer.MAX_VALUE;
	}
}
