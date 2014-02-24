/**
 * 
 */
package stream.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import stream.Data;
import stream.io.QueueService;

/**
 * @author chris
 * 
 */
public class GlobalCollector implements QueueService {

	static int limit = 100000;
	private final static List<Data> globalCollection = new ArrayList<Data>(
			limit);

	String id;

	/**
	 * @see stream.service.Service#reset()
	 */
	@Override
	public void reset() throws Exception {
		synchronized (globalCollection) {
			globalCollection.clear();
		}
	}

	/**
	 * @see stream.io.QueueService#poll()
	 */
	@Override
	public Data poll() {

		synchronized (globalCollection) {
			if (globalCollection.isEmpty())
				return null;

			return globalCollection.remove(0);
		}
	}

	/**
	 * @see stream.io.QueueService#enqueue(stream.Data)
	 */
	@Override
	public boolean enqueue(Data item) {
		synchronized (globalCollection) {
			return globalCollection.add(item);
		}
	}

	public List<Data> getCollection() {
		synchronized (globalCollection) {
			return new ArrayList<Data>(globalCollection);
		}
	}

	@Override
	public Data take() {
		try {
			while (globalCollection.isEmpty()) {
				globalCollection.wait();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return globalCollection.get(0);
	}

	/**
	 * @see stream.io.QueueService#level()
	 */
	@Override
	public int level() {
		return globalCollection.size();
	}

	/**
	 * @see stream.io.QueueService#capacity()
	 */
	@Override
	public int capacity() {
		return limit;
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
		return enqueue(item);
	}

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean write(Collection<Data> data) throws Exception {

		for (Data item : data) {
			enqueue(item);
		}

		// the global collector should never fail to collect an item
		return true;
	}

	/**
	 * @see stream.io.Sink#setId(java.lang.String)
	 */
	@Override
	public void setId(String id) {
	}

	/**
	 * @see stream.io.Sink#init()
	 */
	@Override
	public void init() throws Exception {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see stream.io.Queue#getSize()
	 */
	@Override
	public Integer getSize() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see stream.io.Barrel#clear()
	 */
	@Override
	public int clear() {
		return 0;
	}

	/**
	 * @see stream.io.Source#read()
	 */
	@Override
	public Data read() throws Exception {
		return take();
	}

	@Override
	public void setCapacity(Integer limit) {
		// TODO Auto-generated method stub

	}

	@Override
	public Integer getCapacity() {
		// TODO Auto-generated method stub
		return null;
	}
}