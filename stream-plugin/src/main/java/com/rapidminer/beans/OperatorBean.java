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
	 * @see com.rapidminer.operator.Operator#processStarts()
	 */
	@Override
	public final void processStarts() throws OperatorException {
		super.processStarts();
		this.init();
		this.onProcessStart();
	}

	/**
	 * This method is called at startup of the process, after the parameters
	 * have been set up and before the start of {@link #doWork()}.
	 * 
	 * @throws OperatorException
	 */
	public void onProcessStart() throws OperatorException {

	}

	/**
	 * This method is called at the end of the process.
	 * 
	 * @throws OperatorException
	 */
	public void onProcessEnd() throws OperatorException {

	}

	/**
	 * @see com.rapidminer.operator.Operator#processFinished()
	 */
	@Override
	public final void processFinished() throws OperatorException {
		super.processFinished();
		this.onProcessEnd();
	}

	/**
	 * @see com.rapidminer.operator.Operator#getParameterTypes()
	 */
	@Override
	public List<ParameterType> getParameterTypes() {
		return ParameterTypeDiscovery.getParameterTypes(getClass());
	}
}
