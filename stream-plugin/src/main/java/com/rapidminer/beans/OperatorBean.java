/**
 * 
 */
package com.rapidminer.beans;

import java.util.List;

import stream.plugin.util.ParameterSetup;
import stream.plugin.util.ParameterTypeDiscovery;

import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.parameter.ParameterType;

/**
 * @author chris
 * 
 */
public class OperatorBean extends Operator {

	/**
	 * @param description
	 */
	public OperatorBean(OperatorDescription description) {
		super(description);
	}

	/**
	 * @throws OperatorException
	 */
	public void init() throws OperatorException {
		ParameterSetup.setParameters(this);
	}

	/**
	 * @see com.rapidminer.operator.Operator#getParameterTypes()
	 */
	@Override
	public List<ParameterType> getParameterTypes() {
		return ParameterTypeDiscovery.getParameterTypes(getClass());
	}
}
