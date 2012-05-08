/**
 * 
 */
package com.rapidminer.operator;

import com.rapidminer.annotations.OperatorInfo;
import com.rapidminer.annotations.Parameter;
import com.rapidminer.beans.OperatorBean;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;

/**
 * @author chris
 * 
 */
@OperatorInfo(name = "Do-Nothing-Example-Bean", group = "RapidMiner Beans.Example", text = "This operator bean simply logs a message and passes through any incoming input")
public class ExampleOperatorBean extends OperatorBean {

	final InputPort input = getInputPorts().createPort("input");
	final OutputPort output = getOutputPorts().createPort("output");
	String message;

	/**
	 * @param description
	 */
	public ExampleOperatorBean(OperatorDescription description) {
		super(description);
		getTransformer().addPassThroughRule(input, output);
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
	@Parameter(required = true, description = "A random message, printed out for each processed IO object.")
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @see com.rapidminer.operator.Operator#doWork()
	 */
	@Override
	public void doWork() throws OperatorException {

		//
		// At the time this method is called all parameters have automatically
		// been set using the rapidminer-beans mechanism.
		//
		IOObject in = input.getData(IOObject.class);
		getLogger().info(message);
		output.deliver(in);
	}
}
