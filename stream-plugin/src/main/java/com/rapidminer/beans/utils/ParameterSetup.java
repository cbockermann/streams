/**
 * 
 */
package com.rapidminer.beans.utils;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.runtime.VariableContext;
import stream.runtime.setup.ParameterInjection;

import com.rapidminer.operator.Operator;
import com.rapidminer.operator.UserError;
import com.rapidminer.parameter.ParameterType;

/**
 * @author chris
 * 
 */
public class ParameterSetup {

	static Logger log = LoggerFactory.getLogger(ParameterSetup.class);

	public static void setParameters(Operator op) throws UserError {
		Map<String, ParameterType> types = ParameterTypeFinder
				.getParameterTypes(op.getClass());

		Map<String, String> parameters = new LinkedHashMap<String, String>();
		for (String name : types.keySet()) {
			String value = op.getParameter(name);
			if (value != null)
				parameters.put(name, value);
		}

		try {
			log.info("Injecting parameters {} into operator {}", parameters, op);
			ParameterInjection.inject(op, parameters, new VariableContext());
		} catch (Exception e) {
			e.printStackTrace();
			throw new UserError(op, e.getMessage());
		}
	}
}
