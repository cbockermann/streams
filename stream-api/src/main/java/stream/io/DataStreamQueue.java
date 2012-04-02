/**
 * 
 */
package stream.io;

import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.DataListener;
import stream.data.Processor;

/**
 * @author chris
 * 
 */
public class DataStreamQueue extends AbstractDataStream implements
		DataListener, Processor {

	static Logger log = LoggerFactory.getLogger(DataStreamQueue.class);
	final LinkedBlockingQueue<Data> queue = new LinkedBlockingQueue<Data>();

	public DataStreamQueue() {
	}

	/**
	 * @see stream.io.DataStream#close()
	 */
	@Override
	public void close() {
		queue.clear();
	}

	/**
	 * @see stream.io.AbstractDataStream#readHeader()
	 */
	@Override
	public void readHeader() throws Exception {
	}

	/**
	 * @see stream.io.AbstractDataStream#readItem(stream.data.Data)
	 */
	@Override
	public Data readItem(Data instance) throws Exception {
		Data item = queue.take();
		log.debug("took item from queue: {}", item);
		while (item == null) {
			try {
				log.debug("waiting for item to arrive in queue...");
				Thread.sleep(100);
				item = queue.take();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (item != null)
			instance.putAll(item);
		return instance;
	}

	/**
	 * @see stream.io.AbstractDataStream#readNext()
	 */
	@Override
	public Data readNext() throws Exception {
		Data item = queue.take();
		log.debug("took item from queue: {}", item);
		while (item == null) {
			try {
				log.debug("waiting for item to arrive in queue...");
				Thread.sleep(100);
				item = queue.take();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return item;
	}

	/**
	 * @see stream.data.DataListener#dataArrived(stream.data.Data)
	 */
	@Override
	public void dataArrived(Data item) {
		queue.add(item);
	}

	/**
	 * @see stream.data.Processor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data input) {
		queue.add(input);
		return input;
	}
}