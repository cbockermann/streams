/**
 * 
 */
package stream.plugin.util;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.runtime.VariableContext;
import stream.runtime.setup.ParameterInjection;

import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.UserError;
import com.rapidminer.parameter.ParameterType;

/**
 * @author chris
 * 
 */
public class ParameterSetup {

	static Logger log = LoggerFactory.getLogger(ParameterSetup.class);

	public static void setParameters(Operator op) throws UserError {
		Map<String, ParameterType> types = ParameterTypeDiscovery
				.discoverParameterTypes(op.getClass());

		Map<String, String> parameters = new LinkedHashMap<String, String>();
		for (String name : types.keySet()) {
			String value = op.getParameter(name);
			log.info("Found parameter {} = {}", name, value);
			if (value != null)
				parameters.put(name, value);
			else {
				Object defaultValue = types.get(name).getDefaultValue();
				if (defaultValue != null) {
					parameters.put(name, defaultValue.toString());
				}
			}
		}

		try {
			log.info("Injecting parameters {} into operator {}", parameters, op);
			ParameterInjection.inject(op, parameters, new VariableContext());
		} catch (Exception e) {
			log.error("Failed to inject parameters: {}", e.getMessage());
			e.printStackTrace();
			throw new UserError(op, e.getMessage());
		}
	}

	public static Map<String, String> getParameters(Operator op)
			throws OperatorException {

		Map<String, String> parameters = new LinkedHashMap<String, String>();
		for (ParameterType type : op.getParameterTypes()) {
			String name = type.getKey();
			String value = op.getParameter(name);
			log.info("Found parameter {} = {}", name, value);
			if (value != null)
				parameters.put(name, value);
			else {
				Object defaultValue = type.getDefaultValue();
				if (defaultValue != null) {
					parameters.put(name, defaultValue.toString());
				}
			}
		}
		return parameters;
	}
}
