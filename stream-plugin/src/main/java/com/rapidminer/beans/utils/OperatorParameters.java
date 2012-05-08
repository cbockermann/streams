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
import com.rapidminer.parameter.Parameters;

/**
 * <p>
 * This class provides a method for getting and a method for setting the
 * parameter values of a specified operator.
 * </p>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public class OperatorParameters {

	static Logger log = LoggerFactory.getLogger(OperatorParameters.class);

	/**
	 * Injects the parameter of an operator into the operator using its setter
	 * methods. The parameter values are retrieved from the operator
	 * implementation (i.e. fetched from the RapidMiner GUI).
	 * 
	 * @param op
	 * @throws UserError
	 */
	public static void setParameters(Operator op) throws UserError {
		Map<String, ParameterType> types = ParameterTypeFinder
				.discoverParameterTypes(op.getClass());

		Map<String, String> parameters = new LinkedHashMap<String, String>();
		for (String name : types.keySet()) {
			String value = op.getParameter(name);
			if (value != null)
				parameters.put(name, value);
		}

		try {
			log.debug("Injecting parameters {} into operator {}", parameters,
					op);
			ParameterInjection.inject(op, parameters, new VariableContext());
		} catch (Exception e) {
			e.printStackTrace();
			throw new UserError(op, e.getMessage());
		}
	}

	/**
	 * Returns a map of parameters currently set for the specified operator. The
	 * parameter values are in String format.
	 * 
	 * @param op
	 * @return
	 * @throws Exception
	 */
	public static Map<String, String> getParameters(Operator op)
			throws Exception {
		Map<String, String> parameters = new LinkedHashMap<String, String>();

		Parameters params = op.getParameters();

		for (String key : params.getKeys()) {
			try {
				String value = params.getParameter(key);
				if (value != null) {
					parameters.put(key, value);
				}
			} catch (Exception e) {
				log.error("Failed to get parameter '{}' from operator {}", key,
						op);
			}
		}

		return parameters;
	}
}
