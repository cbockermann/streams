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
import stream.plugin.streaming.GenericStreamingProcessorOperator;
import stream.plugin.streaming.GenericStreamingSourceOperator;
import stream.plugin.util.OperatorHelpFinder;

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

	static boolean rapidMinerStreamingMode = false;
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
				classLoader, iconName, provider, null);

		Description desc = clazz.getAnnotation(Description.class);
		if (desc != null) {
			setFullyQualifiedGroupKey(desc.group());
		}

		libClass = clazz;

		getOperatorDocumentation().setSynopsis("");
		getOperatorDocumentation().setDocumentation("");

		try {
			String html = OperatorHelpFinder.findOperatorHelp(libClass);
			if (html != null) {
				log.debug("Adding operator-documentation:\n{}", html);
				getOperatorDocumentation().setDocumentation(html);
			}
		} catch (Exception e) {
			log.error("Failed to lookup documentation for class '{}': {}",
					libClass, e.getMessage());
			if (log.isDebugEnabled())
				e.printStackTrace();
		}

	}

	/**
	 * @see com.rapidminer.operator.OperatorDescription#createOperatorInstanceByDescription(com.rapidminer.operator.OperatorDescription)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected Operator createOperatorInstanceByDescription(
			OperatorDescription description) throws IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, SecurityException, NoSuchMethodException {

		Operator op = null;

		if (description instanceof GenericOperatorDescription) {
			GenericOperatorDescription sod = (GenericOperatorDescription) description;

			if (DataStream.class.isAssignableFrom(libClass)) {
				log.info("Class {} is a data-stream class!", libClass);

				log.info(
						"Creating GenericStreamingSourceOperator for class {}",
						libClass);

				if (rapidMinerStreamingMode) {
					op = new GenericStreamingSourceOperator(description,
							(Class<? extends DataStream>) sod.libClass);
				} else {
					op = new GenericStreamReader(description,
							(Class<? extends DataStream>) sod.libClass);
				}
				log.info("Operator is of class {}", op.getClass());
				return op;
			}

			log.info("Creating GenericStreamOperator for processor-class {}",
					libClass);

			if (rapidMinerStreamingMode) {
				op = new GenericStreamingProcessorOperator(description,
						(Class<? extends Processor>) sod.libClass);
			} else {
				op = new GenericStreamOperator(description,
						(Class<? extends Processor>) sod.libClass);
			}

			log.info("Operator of class {} is {}", libClass, op.getClass());
			return op;
		}

		log.warn("No support for generic operator instantiation of class {}",
				libClass);
		return super.createOperatorInstanceByDescription(description);
	}

	public static boolean canCreate(Class<?> clazz) {

		if (Processor.class.isAssignableFrom(clazz)) {
			log.info("Yes, we can create an Operator for Processor-class {}",
					clazz);
			return true;
		}

		if (DataStream.class.isAssignableFrom(clazz)) {
			log.info("Yes, we can create an Operator for DataStream-class {}",
					clazz);
			return true;
		}

		log.warn("No generic operator-support for class '{}'", clazz);
		return false;
	}
}
