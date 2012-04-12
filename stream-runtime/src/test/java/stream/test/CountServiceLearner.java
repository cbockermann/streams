/**
 * 
 */
package stream.test;

import stream.AbstractDataProcessor;
import stream.data.Data;

/**
 * @author chris
 * 
 */
public class CountServiceLearner extends AbstractDataProcessor implements
		CountService {

	Long count = 0L;

	/**
	 * @see stream.Processor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data input) {
		count++;
		return input;
	}

	/**
	 * @see stream.test.CountService#getCount()
	 */
	@Override
	public Long getCount() {
		return count;
	}
}
