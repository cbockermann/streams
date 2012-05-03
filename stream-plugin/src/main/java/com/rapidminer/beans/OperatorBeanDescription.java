/**
 * 
 */
package com.rapidminer.beans;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.annotations.Description;

import com.rapidminer.annotations.OperatorInfo;
import com.rapidminer.beans.utils.OperatorHelpFinder;
import com.rapidminer.generic.GenericBeanOperator;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.tools.plugin.Plugin;

/**
 * @author chris
 * 
 */
public class OperatorBeanDescription extends OperatorDescription {

	static Logger log = LoggerFactory.getLogger(OperatorBeanDescription.class);

	final static Map<Class<?>, Class<? extends Operator>> genericOperatorClasses = new LinkedHashMap<Class<?>, Class<? extends Operator>>();
	static {
	}

	final Class<?> beanClass;

	/**
	 * @param fullyQualifiedGroupKey
	 * @param key
	 * @param clazz
	 * @param classLoader
	 * @param iconName
	 * @param provider
	 */
	public OperatorBeanDescription(String fullyQualifiedGroupKey, String key,
			Class<?> clazz, ClassLoader classLoader, String iconName,
			Plugin provider) {
		super(fullyQualifiedGroupKey, key, GenericBeanOperator.class,
				classLoader, iconName, provider, null);

		Description desc = clazz.getAnnotation(Description.class);
		if (desc != null) {
			setFullyQualifiedGroupKey(desc.group());
		}

		beanClass = clazz;

		getOperatorDocumentation().setSynopsis("");
		getOperatorDocumentation().setDocumentation("");

		try {
			String html = OperatorHelpFinder.findOperatorHelp(beanClass);
			if (html != null) {
				log.debug("Adding operator-documentation:\n{}", html);
				getOperatorDocumentation().setDocumentation(html);
			}
		} catch (Exception e) {
			log.error("Failed to lookup documentation for class '{}': {}",
					beanClass, e.getMessage());
			if (log.isDebugEnabled())
				e.printStackTrace();
		}

	}

	/**
	 * @see com.rapidminer.operator.OperatorDescription#createOperatorInstanceByDescription(com.rapidminer.operator.OperatorDescription)
	 */
	@Override
	protected Operator createOperatorInstanceByDescription(
			OperatorDescription description) throws IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, SecurityException, NoSuchMethodException {

		Operator op = null;

		if (description instanceof OperatorBeanDescription) {

			if (Operator.class.isAssignableFrom(beanClass)) {
				@SuppressWarnings("unchecked")
				Constructor<? extends Operator> constr = (Constructor<? extends Operator>) beanClass
						.getConstructor(OperatorDescription.class);
				log.info("Directly instantiating operator of class '{}'",
						beanClass);
				return constr.newInstance(this);
			}

			for (Class<?> clazz : genericOperatorClasses.keySet()) {

				if (clazz.isAssignableFrom(beanClass)) {
					Class<? extends Operator> genericOperatorClass = genericOperatorClasses
							.get(clazz);

					Constructor<? extends Operator> constructor = genericOperatorClass
							.getConstructor(OperatorDescription.class,
									beanClass);
					op = constructor.newInstance(description, beanClass);
					log.info("Operator of class {} is {}", beanClass,
							op.getClass());
					return op;
				}
			}

			log.warn(
					"No generic-operator class could be found for OperatorBean {}",
					beanClass);
		}

		log.warn("No support for generic operator instantiation of class {}",
				beanClass);
		return super.createOperatorInstanceByDescription(description);
	}

	public static boolean canCreate(Class<?> clazz) {

		if (clazz.isAnnotationPresent(OperatorInfo.class)) {
			log.info("Class {} is annotated with @OperatorInfo!", clazz);
		}

		if (clazz.isAnnotationPresent(Description.class)) {
			log.info("Class {} is annotated with @Description!", clazz);
		}

		if (!clazz.isAnnotationPresent(OperatorInfo.class)
				&& !clazz.isAnnotationPresent(Description.class)) {
			return false;
		}

		if (Operator.class.isAssignableFrom(clazz))
			return true;

		for (Class<?> cl : genericOperatorClasses.keySet()) {
			if (cl.isAssignableFrom(clazz)) {
				log.info("Yes, we can create an operator for OperatorBean {}",
						clazz);
			}
		}

		// log.warn("No generic operator-support for class '{}'", clazz);
		return false;
	}
}
