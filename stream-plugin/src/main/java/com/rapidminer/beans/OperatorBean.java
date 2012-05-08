/**
 * 
 */
package com.rapidminer.beans;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapidminer.beans.utils.OperatorParameters;
import com.rapidminer.beans.utils.ParameterTypeFinder;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.parameter.ParameterType;

/**
 * @author chris
 * 
 */
public class OperatorBean extends Operator {

	static Logger log = LoggerFactory.getLogger(OperatorBean.class);

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
		OperatorParameters.setParameters(this);
	}

	/**
	 * @see com.rapidminer.operator.Operator#processStarts()
	 */
	@Override
	public final void processStarts() throws OperatorException {
		log.info("processStarts()");

		log.info("   Calling 'super.processStarts()'");
		super.processStarts();

		log.info("   Calling 'init()'");
		this.init();

		log.info("   Calling 'onProcessStart()'");
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
		return ParameterTypeFinder.getParameterTypes(getClass());
	}
}
