/**
 * 
 */
package stream.flow;

import stream.ProcessorList;
import stream.data.Data;

/**
 * @author chris
 * 
 */
public class Every extends ProcessorList {

	Long n = 1000L;
	Long count = 0L;

	/**
	 * @see stream.ProcessorList#process(stream.data.Data)
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
