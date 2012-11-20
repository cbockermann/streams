/**
 * 
 */
package stream.flow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.ConditionedProcessor;
import stream.Data;
import stream.io.QueueService;

/**
 * @author chris
 * 
 */
public class Redirect extends ConditionedProcessor {

	static Logger log = LoggerFactory.getLogger(Redirect.class);
	QueueService queues[];

	/**
	 * @return the queues
	 */
	public QueueService[] getQueues() {
		return queues;
	}

	/**
	 * @param queues
	 *            the queues to set
	 */
	public void setQueues(QueueService[] queues) {
		this.queues = queues;
	}

	public void setQueue(QueueService queue) {
		setQueues(new QueueService[] { queue });
	}

	/**
	 * @see stream.ConditionedProcessor#processMatchingData(stream.Data)
	 */
	@Override
	public Data processMatchingData(Data data) {

		if (queues == null)
			return null;

		for (QueueService queue : queues) {
			if (queue.enqueue(data)) {
				log.debug("Redirected item to {}", queue);
			} else {
				log.error("Failed to redirect item to {}", queue);
			}
		}

		return null;
	}

}
