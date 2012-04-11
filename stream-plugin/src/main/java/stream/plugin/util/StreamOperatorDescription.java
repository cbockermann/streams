/**
 * 
 */
package stream.plugin.util;

import java.lang.reflect.InvocationTargetException;

import stream.data.DataProcessor;
import stream.plugin.DataStreamOperator;
import stream.runtime.annotations.Description;

import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.tools.plugin.Plugin;

/**
 * @author chris
 * 
 */
public class StreamOperatorDescription extends OperatorDescription {

	final Class<? extends DataProcessor> processorClass;

	/**
	 * @param fullyQualifiedGroupKey
	 * @param key
	 * @param clazz
	 * @param classLoader
	 * @param iconName
	 * @param provider
	 */
	public StreamOperatorDescription(String fullyQualifiedGroupKey, String key,
			Class<? extends DataProcessor> clazz, ClassLoader classLoader,
			String iconName, Plugin provider) {
		super(fullyQualifiedGroupKey, key, DataStreamOperator.class,
				classLoader, iconName, provider);

		Description desc = clazz.getAnnotation(Description.class);
		if (desc != null) {
			setFullyQualifiedGroupKey(desc.group());
		}

		processorClass = clazz;
	}

	/**
	 * @see com.rapidminer.operator.OperatorDescription#createOperatorInstanceByDescription(com.rapidminer.operator.OperatorDescription)
	 */
	@Override
	protected Operator createOperatorInstanceByDescription(
			OperatorDescription description) throws IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, SecurityException, NoSuchMethodException {

		if (description instanceof StreamOperatorDescription) {
			StreamOperatorDescription sod = (StreamOperatorDescription) description;

		}

		return super.createOperatorInstanceByDescription(description);
	}

}
