/**
 * 
 */
package com.rapidminer.beans.utils;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.annotations.BodyContent;
import stream.expressions.Condition;
import stream.runtime.setup.ParameterDiscovery;
import stream.runtime.setup.ServiceInjection;
import stream.service.Service;

import com.rapidminer.annotations.Parameter;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeBoolean;
import com.rapidminer.parameter.ParameterTypeCategory;
import com.rapidminer.parameter.ParameterTypeDouble;
import com.rapidminer.parameter.ParameterTypeFile;
import com.rapidminer.parameter.ParameterTypeInt;
import com.rapidminer.parameter.ParameterTypeList;
import com.rapidminer.parameter.ParameterTypeString;
import com.rapidminer.parameter.ParameterTypeText;
import com.rapidminer.parameter.TextType;

/**
 * This class implements a single utility method for retrieving the correct
 * RapidMiner ParameterType object from a given setter Method.
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public class ParameterTypeFinder {
	static Logger log = LoggerFactory.getLogger(ParameterTypeFinder.class);

	/**
	 * Check the given class for any @parameter annotated fields.
	 * 
	 * @param clazz
	 * @return
	 */
	public static Map<String, ParameterType> getParameterTypes(Class<?> clazz) {

		log.debug("------------------------------------------------------------------------");
		log.debug("Exploring ParameterTypes for class '{}'", clazz);
		Map<String, ParameterType> types = new LinkedHashMap<String, ParameterType>();

		for (Method m : clazz.getMethods()) {

			log.trace("Checking method {}", m);

			if (ServiceInjection.isServiceSetter(m)) {
				log.debug("Found service-setter...");
				String key = m.getName().substring(3, 4).toLowerCase();
				@SuppressWarnings("unchecked")
				Class<? extends Service> serviceType = (Class<? extends Service>) m
						.getParameterTypes()[0];
				types.put(key, new ParameterTypeService(key, "", serviceType));
				continue;
			}

			ParameterInfo info = getParameterInfo(m);
			if (info != null && ParameterDiscovery.isSetter(m)) {
				log.debug("Found setter '{}'", m.getName());
				String key = m.getName().substring(3, 4).toLowerCase();

				if (types.containsKey(key)) {
					log.debug(
							"Already have annotated field for key '{}', skipping setter {}",
							key, m);
					continue;
				}

				if (m.getName().length() > 4)
					key += m.getName().substring(4);

				ParameterType type = ParameterTypeFinder.getParameterType(info,
						key, m.getParameterTypes()[0]);

				if (type != null) {
					log.debug("Adding parameter-type: {}", type);
					types.put(key, type);
					log.debug("  => parameter '{}'", key);
					types.put(key, type);
				}

				Class<?>[] t = m.getParameterTypes();
				if (t[0] == BodyContent.class) {
					log.debug("Found EmbeddedContent parameter, key = '{}'",
							key);
					type = new ParameterTypeText(key, "", TextType.JAVA);
					types.put(key, type);
					continue;
				}
			}
		}

		log.debug("------------------------------------------------------------------------");
		return types;
	}

	protected static ParameterInfo getParameterInfo(Method m) {

		stream.annotations.Parameter param = m
				.getAnnotation(stream.annotations.Parameter.class);
		if (param != null)
			return new ParameterInfo(param);

		Parameter parameter = m.getAnnotation(Parameter.class);
		if (parameter != null)
			return new ParameterInfo(parameter);

		return null;
	}

	/**
	 * This method determines the ParameterType of a setter method based on its
	 * argument type and the parameter annotation. If no parameter annotation is
	 * provided or the argument type cannot be mapped to a parameter type class,
	 * then <code>null</code> is returned.
	 * 
	 * @param param
	 * @param name
	 * @param type
	 * @return
	 */
	protected static ParameterType getParameterType(ParameterInfo param,
			String name, Class<?> type) {

		if (param == null) {
			log.error("Cannot determine the parameter-type without an annotation!");
			return null;
		}

		String desc = "";
		ParameterType pt = null;
		Object defaultValue = parseDefaultValue(param.defaultValue(), type);

		String key = name;
		if (param.name() != null) {
			key = param.name().trim();
			if (key.isEmpty())
				key = name;
		}

		if (param.description() != null) {
			desc = param.description();
		}

		//
		// String parameters
		//
		if (type.equals(String.class)
				|| type.equals(Condition.class)
				|| (type.isArray() && type.getComponentType().equals(
						String.class))) {
			log.debug("ParameterType is a String");

			if (param != null && param.values() != null)
				pt = new ParameterTypeString(key, desc, !param.required());
			else
				pt = new ParameterTypeString(key, desc, false);

			if (param != null && param.values() != null
					&& param.values().length > 0) {
				log.debug("Found category-parameter!");
				pt = new ParameterTypeCategory(key, desc, param.values(), 0);
			}
		}

		//
		// Parameters for doubles
		//
		if (type.equals(Double.class) || type.equals(double.class)) {
			log.debug("ParameterType {} is a Double!");

			pt = new ParameterTypeDouble(key, desc, param.min(), param.max(),
					!param.required());
		}

		//
		// Integer / Long parameters
		//
		if (type.equals(Integer.class) || type.equals(Long.class)
				|| type.equals(int.class) || type.equals(long.class)) {

			log.debug("ParameterType {} is an Integer!", type);

			Integer min = Integer.MIN_VALUE;
			Integer max = Integer.MAX_VALUE;

			if (param != null) {
				try {
					min = new Double(param.min()).intValue();
				} catch (Exception e) {
					min = Integer.MIN_VALUE;
				}
				try {
					max = new Double(param.max()).intValue();
				} catch (Exception e) {
					max = Integer.MAX_VALUE;
				}
			}

			pt = new ParameterTypeInt(key, desc, min, max, !param.required());
		}

		//
		// Boolean parameters
		//
		if (type.equals(Boolean.class) || type.equals(boolean.class)) {
			log.debug("ParameterType {} is a Boolean!");
			pt = new ParameterTypeBoolean(key, desc, !param.required());
		}

		if (type.equals(File.class)) {
			pt = new ParameterTypeFile(key, desc, null, !param.required());
		}

		//
		// Map parameters
		//
		if (Map.class.isAssignableFrom(type)) {
			log.debug("Found Map parameter... ");
			pt = new ParameterTypeList(key, desc, new ParameterTypeString(
					"key", ""), new ParameterTypeString("value", ""));
			return pt;
		}

		if (defaultValue != null)
			pt.setDefaultValue(defaultValue);

		return pt;
	}

	private static Object parseDefaultValue(String defaultValue, Class<?> type) {
		try {
			if (defaultValue == null)
				return null;
			Constructor<?> constructor = type.getConstructor(String.class);
			return constructor.newInstance(defaultValue);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}