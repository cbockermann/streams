/**
 * 
 */
package stream.plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.plugin.util.ParameterTypeDiscovery;
import stream.runtime.VariableContext;
import stream.runtime.setup.ParameterInjection;

import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.UserError;
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
	 * @see com.rapidminer.operator.Operator#getParameterTypes()
	 */
	@Override
	public List<ParameterType> getParameterTypes() {
		List<ParameterType> types = super.getParameterTypes();
		Map<String, ParameterType> pt = ParameterTypeDiscovery
				.discoverParameterTypes(this.getClass());
		for (String key : pt.keySet()) {
			types.add(pt.get(key));
		}
		return types;
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
			ParameterInjection.inject(this, params, new VariableContext());
		} catch (Exception e) {
			log.error(
					"Failed to set parameters for DataStream Operator '{}': {}",
					this.getClass(), e.getMessage());
			throw new UserError(this, e, -1);
		}
	}
}