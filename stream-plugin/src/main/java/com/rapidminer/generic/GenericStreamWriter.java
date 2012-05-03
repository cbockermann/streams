/**
 * 
 */
package com.rapidminer.generic;

import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;

/**
 * @author chris
 * 
 */
public class GenericStreamWriter extends Operator {

	final InputPort input = getInputPorts().createPort("data item");
	final OutputPort output = getOutputPorts().createPort("data item");

	/**
	 * @param description
	 */
	public GenericStreamWriter(OperatorDescription description) {
		super(description);
	}
}