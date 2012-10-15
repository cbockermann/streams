/**
 * 
 */
package stream.service;

import java.util.ArrayList;
import java.util.List;

import stream.data.Data;
import stream.io.QueueService;

/**
 * @author chris
 * 
 */
public class GlobalCollector implements QueueService {

	private final static List<Data> globalCollection = new ArrayList<Data>();

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
	 * @see stream.io.QueueService#enqueue(stream.data.Data)
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
			while( globalCollection.isEmpty() ){
				globalCollection.wait();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return globalCollection.get(0);
	}
}