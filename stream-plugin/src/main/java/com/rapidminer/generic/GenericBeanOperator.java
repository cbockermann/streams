/**
 * 
 */
package com.rapidminer.generic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.runtime.VariableContext;
import stream.runtime.setup.ParameterInjection;

import com.rapidminer.beans.utils.ParameterTypeFinder;
import com.rapidminer.operator.IOObject;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.UserError;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.ParameterType;

/**
 * @author chris
 * 
 */
public class GenericBeanOperator extends Operator {

	static Logger log = LoggerFactory.getLogger(GenericBeanOperator.class);

	final Class<?> beanClass;
	final List<ParameterType> parameterTypes = new ArrayList<ParameterType>();

	final InputPort input = getInputPorts().createPort("input");
	final OutputPort output = getOutputPorts().createPort("output");

	Object bean;

	/**
	 * @param description
	 */
	public GenericBeanOperator(OperatorDescription description,
			Class<?> beanClass) {
		super(description);
		this.beanClass = beanClass;

		Map<String, ParameterType> types = ParameterTypeFinder
				.getParameterTypes(beanClass);

		for (String key : types.keySet()) {
			parameterTypes.add(types.get(key));
		}

		getTransformer().addPassThroughRule(input, output);
	}

	/**
	 * @see com.rapidminer.operator.Operator#processStarts()
	 */
	@Override
	public void processStarts() throws OperatorException {
		super.processStarts();

		log.debug("Setting up stream-processor for the first time (init)");
		Map<String, String> params = new HashMap<String, String>();

		List<ParameterType> types = this.getParameterTypes();
		for (ParameterType type : types) {
			String key = type.getKey();
			String value = getParameter(key);
			log.info("Preparing parameter {} = {}", key, value);
			if (key != null && value != null) {
				params.put(key, value);
			}
		}

		try {
			ParameterInjection.inject(bean, params, new VariableContext());
		} catch (Exception e) {
			log.error(
					"Failed to set parameters for DataStream Operator '{}': {}",
					bean.getClass(), e.getMessage());
			throw new UserError(this, e, -1);
		}
	}

	/**
	 * @see com.rapidminer.operator.Operator#doWork()
	 */
	@Override
	public void doWork() throws OperatorException {

		IOObject in = input.getData(IOObject.class);

		super.doWork();
	}
}