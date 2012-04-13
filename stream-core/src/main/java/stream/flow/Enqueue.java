/**
 * 
 */
package stream.flow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.AbstractProcessor;
import stream.data.Data;
import stream.io.QueueService;

/**
 * @author chris
 * 
 */
public class Enqueue extends AbstractProcessor {

	static Logger log = LoggerFactory.getLogger(Enqueue.class);
	String ref = null;

	QueueService queue;

	public void setQueue(QueueService queue) {
		this.queue = queue;
	}

	/**
	 * @see stream.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data data) {

		if (queue == null) {
			log.error("No QueueService injected!");
			return data;
		}

		queue.enqueue(data);
		return data;
	}
}
