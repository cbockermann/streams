/**
 * 
 */
package stream.io;

import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.data.SequenceID;

/**
 * <p>
 * An implementation of an ordered queue of data items.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 * 
 */
public class OrderedQueue implements Queue {

	static Logger log = LoggerFactory.getLogger(OrderedQueue.class);
	protected String sequenceKey = "@source:item";
	protected boolean closed = false;
	protected Integer limit = 1000;
	protected String id;
	protected Object lock = new Object();

	protected SequenceID nextOut = new SequenceID();
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
	public boolean write(Data item) throws Exception {

		synchronized (lock) {

			if (closed) {
				log.error("Failed to write to closed ordered-queue {}", getId());
				return false;
			}

			SequenceID id = null;

			try {
				id = (SequenceID) item.get(sequenceKey);
				if (id == null)
					throw new Exception(
							"Item does not provide sequence-id - required for ordered queue insertion!");
			} catch (Exception e) {
				log.error(
						"Failed to determine sequence ID from item at key '{}': {}",
						sequenceKey, e.getMessage());
				if (log.isDebugEnabled()) {
					e.printStackTrace();
				}
				throw e;
			}

			// the inserted item is right the next one that is required
			// to be pushed out for an ordered sequence.
			//
			if (nextOut.compareTo(id) == 0) {
				nextItem = item;
				lock.notifyAll();
				return true;
			}

			// Insert the item at the right ordered spot
			//
			for (int i = 0; i < queue.size(); i++) {

				Data cur = queue.get(i);
				if (cur != null) {

					SequenceID curId = (SequenceID) cur.get(sequenceKey);
					if (curId.compareTo(id) > 0) {
						queue.add(i, item);
						return true;
					}
				}
			}
		}
		queue.add(item);
		return true;
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
			// log.debug("Queue contents: {}", queue);

			// nextItem will automagically be set in the "write"
			// method calls
			//
			log.debug("nextItem: {}", nextItem);
			while (!closed && nextItem == null) {
				lock.wait();
			}

			if (closed && nextItem == null)
				return null;

			Data item = nextItem;
			log.debug("Returning item: {}", item);
			nextOut.increment();
			log.debug("Next SequenceID is: {}", nextOut);
			nextItem = findNext();
			return item;
		}
	}

	private Data findNext() {
		log.debug("looking for next item with ID '{}'", nextOut);
		for (int i = 0; i < queue.size(); i++) {
			Data cur = queue.get(i);
			SequenceID id = (SequenceID) cur.get(sequenceKey);
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
	 * @see stream.io.Queue#setSize(java.lang.Integer)
	 */
	@Override
	public void setSize(Integer limit) {
		this.limit = limit;
		this.queue = new ArrayList<Data>(limit);
	}

	/**
	 * @see stream.io.Queue#getSize()
	 */
	@Override
	public Integer getSize() {
		return limit;
	}

	@Override
	public boolean write(Collection<Data> data) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}
}