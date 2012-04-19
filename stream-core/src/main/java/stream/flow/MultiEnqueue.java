/**
 * 
 */
package stream.flow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.AbstractProcessor;
import stream.ProcessContext;
import stream.data.Data;
import stream.io.QueueService;

/**
 * @author Hendrik Blom
 * 
 */
public class MultiEnqueue extends AbstractProcessor {

	static Logger log = LoggerFactory.getLogger(MultiEnqueue.class);
	String ref = null;

	protected String[] queuesNames;
	protected QueueService[] queues;

	public void setQueues(String[] queues) {
		this.queuesNames = queues;
	}

	public String[] getKeys() {
		return queuesNames;
	}

	@Override
	public void init(ProcessContext ctx) throws Exception {
		super.init(ctx);
		queues = new QueueService[queuesNames.length];
		for (int i = 0; i < queuesNames.length; i++) {
			queues[i] = (QueueService) context.lookup(queuesNames[i]);
		}
	}

	/**
	 * @see stream.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data data) {

		if (queues == null || queues.length == 0) {
			log.error("No QueueService injected!");
			return data;
		}

		enqueue(data);
		return data;
	}

	protected void enqueue(Data data) {
		for (QueueService qs : queues) {
			qs.enqueue(data);
		}
	}
}
