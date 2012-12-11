/**
 * 
 */
package stream.flow;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.io.QueueService;

/**
 * @author chris
 * 
 */
public class EnqueueTestAssertion implements QueueService {

	static Logger log = LoggerFactory.getLogger(EnqueueTestAssertion.class);
	public static List<Data> collection = new ArrayList<Data>();

	/**
	 * @see stream.service.Service#reset()
	 */
	@Override
	public void reset() throws Exception {
		collection.clear();
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
		synchronized (collection) {
			log.info("Item added to global collection: {}", item);
			collection.add(item);
		}
		return true;
	}

	/**
	 * @see stream.io.QueueService#level()
	 */
	@Override
	public int level() {
		return collection.size();
	}

	/**
	 * @see stream.io.QueueService#capacity()
	 */
	@Override
	public int capacity() {
		return Integer.MAX_VALUE;
	}
}