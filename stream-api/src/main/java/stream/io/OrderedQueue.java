/**
 * 
 */
package stream.io;

import java.util.ArrayList;

import stream.Data;
import stream.data.LongSequenceID;

/**
 * <p>
 * An implementation of an ordered queue of data items.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 * 
 */
public class OrderedQueue implements Queue {

	protected boolean closed = false;
	protected Integer limit = 1000;
	protected String id;
	protected Object lock = new Object();

	protected LongSequenceID nextOut;
	protected Data nextItem;
	protected ArrayList<Data> queue = new ArrayList<Data>(limit);

	/**
	 * @see stream.io.Barrel#clear()
	 */
	@Override
	public int clear() {
		return 0;
	}

	/**
	 * @see stream.io.Sink#getId()
	 */
	@Override
	public String getId() {
		return id;
	}

	/**
	 * @see stream.io.Sink#write(stream.Data)
	 */
	@Override
	public void write(Data item) throws Exception {

		synchronized (lock) {

			if (closed) {
				throw new Exception("Failed to write to closed ordered-queue "
						+ this + "#" + getId());
			}

			LongSequenceID id = (LongSequenceID) item.get("@source#id");
			if (id == null)
				throw new Exception(
						"Item does not provide sequence-id - required for ordered queue insertion!");

			// the inserted item is right the next one that is required
			// to be pushed out for an ordered sequence.
			//
			if (nextOut.compareTo(id) == 0) {
				nextItem = item;
				lock.notifyAll();
				return;
			}

			// Insert the item at the right ordered spot
			//
			for (int i = 0; i < queue.size(); i++) {

				Data cur = queue.get(i);
				if (cur != null) {

					LongSequenceID curId = (LongSequenceID) cur.get("@source#id");
					if (curId.compareTo(id) > 0) {
						queue.add(i, item);
						return;
					}
				}
			}
		}
		queue.add(item);
	}

	/**
	 * @see stream.io.Source#setId(java.lang.String)
	 */
	@Override
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @see stream.io.Source#init()
	 */
	@Override
	public void init() throws Exception {

	}

	/**
	 * @see stream.io.Source#read()
	 */
	@Override
	public Data read() throws Exception {

		synchronized (lock) {
			while (nextItem == null) {
				lock.wait();
			}

			Data item = nextItem;
			nextOut.nextValue();
			nextItem = findNext();
			return item;
		}
	}

	private Data findNext() {
		for (int i = 0; i < queue.size(); i++) {
			Data cur = queue.get(i);
			LongSequenceID id = (LongSequenceID) cur.get("@source#id");
			if (id.compareTo(nextOut) == 0) {
				nextItem = cur;
				queue.remove(i);
				return nextItem;
			}

			if (id.compareTo(nextOut) > 0) {
				break;
			} else {
				queue.remove(i);
			}
		}
		return null;
	}

	/**
	 * @see stream.io.Source#close()
	 */
	@Override
	public void close() throws Exception {
		closed = true;
		synchronized (lock) {
			lock.notifyAll();
		}
	}

	/**
	 * @see stream.io.Queue#setLimit(java.lang.Integer)
	 */
	@Override
	public void setLimit(Integer limit) {
		this.limit = limit;
		this.queue = new ArrayList<Data>(limit);
	}

	/**
	 * @see stream.io.Queue#getLimit()
	 */
	@Override
	public Integer getLimit() {
		return limit;
	}

	/**
	 * @see stream.io.Queue#poll()
	 */
	@Override
	public Data poll() {
		synchronized (lock) {

			if (closed)
				return null;

			if (nextItem == null) {
				return null;
			} else {

				Data item = nextItem;
				nextItem = this.findNext();
				return item;
			}
		}
	}
}