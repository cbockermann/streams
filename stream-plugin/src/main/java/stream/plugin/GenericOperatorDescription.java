/**
 * 
 */
package stream.plugin;

import java.lang.reflect.InvocationTargetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Processor;
import stream.annotations.Description;
import stream.io.DataStream;

import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.tools.plugin.Plugin;

/**
 * @author chris
 * 
 */
public class GenericOperatorDescription extends OperatorDescription {

	static Logger log = LoggerFactory
			.getLogger(GenericOperatorDescription.class);
	final Class<?> libClass;

	/**
	 * @param fullyQualifiedGroupKey
	 * @param key
	 * @param clazz
	 * @param classLoader
	 * @param iconName
	 * @param provider
	 */
	public GenericOperatorDescription(String fullyQualifiedGroupKey,
			String key, Class<?> clazz, ClassLoader classLoader,
			String iconName, Plugin provider) {
		super(fullyQualifiedGroupKey, key, GenericStreamOperator.class,
				classLoader, iconName, provider);

		Description desc = clazz.getAnnotation(Description.class);
		if (desc != null) {
			setFullyQualifiedGroupKey(desc.group());
		}

		libClass = clazz;
	}

	/**
	 * @see com.rapidminer.operator.OperatorDescription#createOperatorInstanceByDescription(com.rapidminer.operator.OperatorDescription)
	 */
	@Override
	protected Operator createOperatorInstanceByDescription(
			OperatorDescription description) throws IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, SecurityException, NoSuchMethodException {

		if (description instanceof GenericOperatorDescription) {
			GenericOperatorDescription sod = (GenericOperatorDescription) description;

			if (DataStream.class.isAssignableFrom(libClass)) {
				log.info("Class {} is a data-stream class!", libClass);
				@SuppressWarnings("unchecked")
				GenericStreamReader reader = new GenericStreamReader(
						description,
						(Class<? extends stream.io.DataStream>) libClass);
				log.info("Created GenericStreamReader for '{}'", libClass);
				return reader;
			}

			log.info("Creating GenericStreamOperator for processor-class {}",
					libClass);
			DataStreamOperator op = new GenericStreamOperator(description,
					sod.libClass);
			return op;
		}

		log.info("Calling super.createOperatorInstanceByDescription(...)");
		return super.createOperatorInstanceByDescription(description);
	}

	public static boolean canCreate(Class<?> clazz) {

		if (Processor.class.isAssignableFrom(clazz)) {
			return true;
		}

		if (DataStream.class.isAssignableFrom(clazz)) {
			return true;
		}

		log.warn("No generic operator-support for class '{}'", clazz);
		return false;
	}
}
