/**
 * 
 */
package stream.data.stats;

import stream.AbstractProcessor;
import stream.data.Data;

/**
 * @author chris
 * 
 */
public class CountServiceLearner extends AbstractProcessor implements
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
	 * @see stream.data.stats.CountService#getCount()
	 */
	@Override
	public Long getCount() {
		return count;
	}
}
