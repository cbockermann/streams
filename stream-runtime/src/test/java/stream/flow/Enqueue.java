/**
 * 
 */
package stream.flow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Processor;
import stream.data.Data;
import stream.data.DataFactory;
import stream.io.QueueService;

/**
 * @author chris
 * 
 */
public class Enqueue implements Processor {

	static Logger log = LoggerFactory.getLogger(Enqueue.class);
	QueueService[] queues;

	/**
	 * @param queues
	 *            the queues to set
	 */
	public void setQueues(QueueService[] queues) {
		this.queues = queues;
	}

	public void setQueue(QueueService queue) {
		this.queues = new QueueService[] { queue };
	}

	/**
	 * @see stream.Processor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data input) {

		if (input == null) {
			return input;
		}

		if (queues == null) {
			log.info("No queues specified!");
			return input;
		}

		for (int i = 0; i < queues.length; i++) {
			if (queues[i] != null) {
				log.info("Enqueuing item into {}", queues[i]);
				queues[i].enqueue(DataFactory.create(input));
			}
		}

		return input;
	}
}
