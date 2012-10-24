/**
 * 
 */
package stream.flow;

import stream.Data;
import stream.ProcessorList;

/**
 * @author chris
 * 
 */
public class Every extends ProcessorList {

	Long n = 1000L;
	Long count = 0L;

	/**
	 * @see stream.ProcessorList#process(stream.Data)
	 */
	@Override
	public Data process(Data input) {
		count++;

		if (n != null && count % n == 0) {
			return super.process(input);
		}

		return input;
	}

	/**
	 * @return the n
	 */
	public Long getN() {
		return n;
	}

	/**
	 * @param n
	 *            the n to set
	 */
	public void setN(Long n) {
		this.n = n;
	}
}
