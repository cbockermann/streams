/**
 * 
 */
package stream.data;

import stream.Processor;

/**
 * @author chris
 * 
 */
public class PrintCounter implements Processor {

	CounterService counter;

	/**
	 * @return the counter
	 */
	public CounterService getCounter() {
		return counter;
	}

	/**
	 * @param counter
	 *            the counter to set
	 */
	public void setCounter(CounterService counter) {
		this.counter = counter;
	}

	/**
	 * @see stream.Processor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data input) {

		if (counter != null) {
			System.out.println("Counter has value: " + counter.getCount());
			if (input != null) {
				input.put("counter", counter.getCount());
			}
		}

		return input;
	}
}
