/**
 * 
 */
package stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.io.Queue;
import stream.io.QueueService;

/**
 * @author chris
 * 
 */
public class QueueServiceWrapper implements QueueService, Queue {

	static Logger log = LoggerFactory.getLogger(QueueServiceWrapper.class);
	private Queue queue;

	/**
	 * @return
	 * @see stream.io.Source#getId()
	 */
	public String getId() {
		return queue.getId();
	}

	/**
	 * @param id
	 * @see stream.io.Source#setId(java.lang.String)
	 */
	public void setId(String id) {
		queue.setId(id);
	}

	/**
	 * @param item
	 * @throws Exception
	 * @see stream.io.Sink#write(stream.Data)
	 */
	public void write(Data item) throws Exception {
		queue.write(item);
	}

	/**
	 * @throws Exception
	 * @see stream.io.Source#init()
	 */
	public void init() throws Exception {
		queue.init();
	}

	/**
	 * @return
	 * @see stream.io.Barrel#clear()
	 */
	public int clear() {
		return queue.clear();
	}

	/**
	 * @param limit
	 * @see stream.io.Queue#setLimit(java.lang.Integer)
	 */
	public void setLimit(Integer limit) {
		queue.setLimit(limit);
	}

	/**
	 * @return
	 * @see stream.io.Queue#getLimit()
	 */
	public Integer getLimit() {
		return queue.getLimit();
	}

	/**
	 * @return
	 * @throws Exception
	 * @see stream.io.Source#read()
	 */
	public Data read() throws Exception {
		return queue.read();
	}

	/**
	 * @throws Exception
	 * @see stream.io.Source#close()
	 */
	public void close() throws Exception {
		queue.close();
	}

	public QueueServiceWrapper(Queue queue) {
		this.queue = queue;
	}

	/**
	 * @see stream.service.Service#reset()
	 */
	@Override
	public void reset() throws Exception {
		queue.clear();
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
			return queue.read();
		} catch (Exception e) {
			if (log.isDebugEnabled())
				e.printStackTrace();
			return null;
		}
	}

	/**
	 * @see stream.io.QueueService#enqueue(stream.Data)
	 */
	@Override
	public boolean enqueue(Data item) {
		try {
			this.queue.write(item);
		} catch (Exception e) {
			if (log.isDebugEnabled())
				e.printStackTrace();
			return false;
		}
		return true;
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
		// TODO Auto-generated method stub
		return 0;
	}
}
