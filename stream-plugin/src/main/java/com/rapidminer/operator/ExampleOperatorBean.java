/**
 * 
 */
package com.rapidminer.operator;

import com.rapidminer.annotations.OperatorInfo;
import com.rapidminer.annotations.Parameter;

/**
 * @author chris
 * 
 */
@OperatorInfo(group = "", text = "")
public class ExampleOperatorBean extends Operator {

	String message;

	/**
	 * @param description
	 */
	public ExampleOperatorBean(OperatorDescription description) {
		super(description);
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message
	 *            the message to set
	 */
	@Parameter(required = true)
	public void setMessage(String message) {
		this.message = message;
	}
}
