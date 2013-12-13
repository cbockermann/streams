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
public class Wait extends ProcessorList {

	protected long n;
	long count;

	public Wait() {
		n = 1000l;
		count = 0l;
	}

	/**
	 * @see stream.ProcessorList#process(stream.Data)
	 */
	@Override
	public Data process(Data input) {
		count++;
		if (count > n) {
			count = 0l;
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
