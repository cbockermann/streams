/**
 * 
 */
package stream.test;

import stream.Data;
import stream.Processor;

/**
 * @author chris
 * 
 */
public class CheckVariable implements Processor {

	String variable = "";
	String expected = "";

	/**
	 * @see stream.Processor#process(stream.Data)
	 */
	@Override
	public Data process(Data input) {
		if (variable.equals(expected)) {
			throw new RuntimeException("Variable check failed! Expected '"
					+ expected + "', found '" + variable + "'");
		}

		return input;
	}

	/**
	 * @return the variable
	 */
	public String getVariable() {
		return variable;
	}

	/**
	 * @param variable
	 *            the variable to set
	 */
	public void setVariable(String variable) {
		this.variable = variable;
	}

	/**
	 * @return the expected
	 */
	public String getExpected() {
		return expected;
	}

	/**
	 * @param expected
	 *            the expected to set
	 */
	public void setExpected(String expected) {
		this.expected = expected;
	}

}
