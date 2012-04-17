/**
 * 
 */
package stream.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.ProcessContext;
import stream.StatefulProcessor;
import stream.data.Data;

/**
 * @author chris
 * 
 */
public class ProcessContextCounter implements StatefulProcessor {

	static Logger log = LoggerFactory.getLogger(ProcessContextCounter.class);
	ProcessContext context;

	/**
	 * @see stream.Processor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data input) {
		log.info("Using context {}", context);
		Long count = (Long) context.get("count");
		if (count == null) {
			count = 1L;
		} else {
			count++;
		}
		context.set("count", count);
		return input;
	}

	/**
	 * @see stream.StatefulProcessor#init(stream.ProcessContext)
	 */
	@Override
	public void init(ProcessContext context) throws Exception {
		this.context = context;
		log.info("Initializing context to {}", this.context);
		this.context.set("count", 1L);
	}

	/**
	 * @see stream.StatefulProcessor#resetState()
	 */
	@Override
	public void resetState() throws Exception {
		context.set("count", 1L);
	}

	/**
	 * @see stream.StatefulProcessor#finish()
	 */
	@Override
	public void finish() throws Exception {
	}
}
