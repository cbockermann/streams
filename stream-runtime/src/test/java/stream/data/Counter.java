/**
 * 
 */
package stream.data;

import stream.Data;
import stream.Processor;

/**
 * @author chris
 * 
 */
public class Counter implements CounterService, Processor {

	Long count = 0L;

	/**
	 * @see stream.service.Service#reset()
	 */
	@Override
	public void reset() throws Exception {
		count = 0L;
	}

	/**
	 * @see stream.Processor#process(stream.Data)
	 */
	@Override
	public Data process(Data input) {
		if (input != null)
			count++;
		return input;
	}

	/**
	 * @see stream.data.CounterService#getCount()
	 */
	@Override
	public Long getCount() {
		return new Long(count);
	}
}
