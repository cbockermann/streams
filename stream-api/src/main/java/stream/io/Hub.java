/**
 * 
 */
package stream.io;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.data.DataFactory;

/**
 * <p>
 * A hub is simply a queue that dispatches incoming events to various listeners.
 * Listeners can dynamically register at a hub and unregister later on. The hub
 * will call the <code>dataArrived</code> method of each listener and provide a
 * copy of the data item.
 * </p>
 * <p>
 * The hub will not queue any events or keep them in memory. If data is enqueued
 * into the hub and no listener is registered, that data is simply discarded.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 * 
 */
public class Hub implements QueueService, HubService {

	static Logger log = LoggerFactory.getLogger(Hub.class);
	final List<DataStreamListener> listener = new CopyOnWriteArrayList<DataStreamListener>();

	String id;

	/**
	 * @see stream.service.Service#reset()
	 */
	@Override
	public void reset() throws Exception {
	}

	/**
	 * @see stream.io.QueueService#poll()
	 */
	@Override
	public Data poll() {
		return null;
	}

	/**
	 * @see stream.io.QueueService#take()
	 */
	@Override
	public Data take() {
		return null;
	}

	/**
	 * @see stream.io.QueueService#enqueue(stream.Data)
	 */
	@Override
	public boolean enqueue(Data item) {
		//
		// ENH: use a pool of dispatcher threads, which simultaneously
		// dispatch the items to more than one listener at once.
		//
		for (DataStreamListener dsl : listener) {
			try {
				dsl.dataArrived(DataFactory.create(item));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return true;
	}

	/**
	 * @see stream.io.HubService#register(stream.io.DataStreamListener)
	 */
	@Override
	public void register(DataStreamListener listener) throws Exception {
		this.listener.add(listener);
	}

	/**
	 * @see stream.io.HubService#unregister(stream.io.DataStreamListener)
	 */
	@Override
	public void unregister(DataStreamListener listener) throws Exception {
		this.listener.remove(listener);
	}

	/**
	 * @see stream.io.QueueService#level()
	 */
	@Override
	public int level() {
		return 0;
	}

	/**
	 * @see stream.io.QueueService#capacity()
	 */
	@Override
	public int capacity() {
		return 1;
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
	 * @see stream.io.Sink#write(stream.Data)
	 */
	@Override
	public boolean write(Data item) throws Exception {
		return this.enqueue(item);
	}

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean write(Collection<Data> data) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see stream.io.Sink#init()
	 */
	@Override
	public void init() throws Exception {
	}
}