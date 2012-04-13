/**
 * 
 */
package stream.test;

import stream.AbstractProcessor;
import stream.data.Data;

/**
 * @author chris
 * 
 */
public class CountCheck extends AbstractProcessor {

	Long myCount = 0L;

	CountService countService = null;

	/**
	 * @return the countService
	 */
	public CountService getCountService() {
		return countService;
	}

	/**
	 * @param countService
	 *            the countService to set
	 */
	public void setCountService(CountService countService) {
		this.countService = countService;
	}

	/**
	 * @see stream.Processor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data input) {
		myCount++;

		if (countService == null)
			throw new RuntimeException("No countService has been injected!");

		Long count = countService.getCount();
		if (!myCount.equals(count)) {
			throw new RuntimeException("Count of CountService mismatches!");
		}

		return input;
	}
}
