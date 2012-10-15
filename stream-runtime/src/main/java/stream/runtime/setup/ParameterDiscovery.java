/*
 *  streams library
 *
 *  Copyright (C) 2011-2012 by Christian Bockermann, Hendrik Blom
 * 
 *  streams is a library, API and runtime environment for processing high
 *  volume data streams. It is composed of three submodules "stream-api",
 *  "stream-core" and "stream-runtime".
 *
 *  The streams library (and its submodules) is free software: you can 
 *  redistribute it and/or modify it under the terms of the 
 *  GNU Affero General Public License as published by the Free Software 
 *  Foundation, either version 3 of the License, or (at your option) any 
 *  later version.
 *
 *  The stream.ai library (and its submodules) is distributed in the hope
 *  that it will be useful, but WITHOUT ANY WARRANTY; without even the implied 
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/.
 */
package stream.runtime.setup;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.annotations.Parameter;

/**
 * This class implements an annotation-based parameter-type discovery. This
 * allows for annotating class fields and automatically adding these fields to
 * the RapidMiner operator object.
 * 
 * @author Christian Bockermann
 * 
 */
public class ParameterDiscovery {

	/* The global logger for this class */
	static Logger log = LoggerFactory.getLogger(ParameterDiscovery.class);

	/**
	 * Check the given class for any @parameter annotated fields.
	 * 
	 * @param clazz
	 * @return
	 */
	public static Map<String, Class<?>> discoverParameters(Class<?> clazz) {

		Map<String, Class<?>> types = new LinkedHashMap<String, Class<?>>();

		for (Method m : clazz.getMethods()) {

			if (ParameterDiscovery.isSetter(m)) {

				log.info("Found setter '{}'", m.getName());
				String key = m.getName().substring(3, 4).toLowerCase();
				if (m.getName().length() > 4)
					key += m.getName().substring(4);

				Parameter param = m.getAnnotation(Parameter.class);
				if (param != null) {
					log.info("setter-method is annotated: {}", param);
					if (param.name() != null && !param.name().isEmpty()) {
						key = param.name();
					}
				}

				if (!types.containsKey(key)) {
					log.info("  => parameter '{}'", key);
					types.put(key, m.getParameterTypes()[0]);
				} else
					log.info(
							"Parameter {} already defined by field-annotation",
							key);
			}
		}

		return types;
	}

	/**
	 * This method returns the parameter annotation from the given class for the
	 * specified parameter key. If attribute has been annotated with the given
	 * key as name, then this method will return <code>null</code>.
	 * 
	 * @param clazz
	 * @param key
	 * @return
	 */
	public static Parameter getParameterAnnotation(Class<?> clazz, String key) {

		for (Method m : clazz.getMethods()) {
			if (ParameterDiscovery.isSetter(m)
					&& m.getName().toLowerCase()
							.equals("set" + key.toLowerCase())) {
				Parameter p = m.getAnnotation(Parameter.class);
				log.debug("Found parameter annotation for class {}, key {}: "
						+ p, clazz, key);
				return p;
			}
		}
		return null;
	}

	public static Class<?> getParameterType(Class<?> clazz, String name) {

		for (Method m : clazz.getMethods()) {
			if (ParameterDiscovery.isSetter(m)
					&& m.getName().toLowerCase()
							.equals("set" + name.toLowerCase())) {
				return m.getParameterTypes()[0];
			}
		}

		return null;
	}

	public static List<Parameter> discoverParameterAnnotations(Class<?> clazz) {
		List<Parameter> parameters = new ArrayList<Parameter>();
		Field[] fields = clazz.getDeclaredFields();

		log.info("Found {} fields", fields.length);

		for (Method m : clazz.getMethods()) {
			Parameter param = m.getAnnotation(Parameter.class);
			if (param != null) {
				log.info("Found @parameter annotated field '{}'", m.getName());
				log.info("    field.getType() = {}", (Object[]) m.getParameterTypes());
				parameters.add(param);
			} else {
				log.info("Field '{}' is not annotated as parameter",
						m.getName());
			}
		}
		return parameters;
	}

	public static String getParameterName(Method m) {
		if (isGetter(m)) {

			String key = m.getName().substring(3, 4).toLowerCase();
			if (m.getName().length() > 3)
				key += m.getName().substring(4);

			return key;
		}

		return null;
	}

	/**
	 * This method defines whether a method matches all requirements of being a
	 * get-Method. Basically this requires the name to start with
	 * <code>get</code> and the return type to be any of the ones supported for
	 * injection.
	 * 
	 * @param m
	 * @return
	 */
	public static boolean isGetter(Method m) {
		if (m.getName().toLowerCase().startsWith("get")) {
			Class<?> rt = m.getReturnType();
			if (ParameterInjection.isTypeSupported(rt))
				return true;
		}
		return false;
	}

	/**
	 * This method defines whether a method matches all requirements of being a
	 * get-Method. Basically this requires the name to start with
	 * <code>set</code> and the single (!) parameter type to be any of the ones
	 * supported for injection.
	 * 
	 * @param m
	 * @return
	 */
	public static boolean isSetter(Method m) {
		if (m.getName().toLowerCase().startsWith("set")
				&& m.getParameterTypes().length == 1) {
			Class<?> rt = m.getParameterTypes()[0];
			if (ParameterInjection.isTypeSupported(rt))
				return true;
		}
		return false;
	}

	public static Map<String, String> getProperties(String prefix, Properties p) {
		String pre = prefix;
		if (!pre.endsWith("."))
			pre = pre + ".";

		Map<String, String> params = new HashMap<String, String>();

		for (Object o : p.keySet()) {
			String key = o.toString();
			if (key.startsWith(pre)) {
				params.put(key.substring(pre.length()), p.getProperty(key));
			}
		}

		return params;
	}

	public static Map<String, String> getSystemProperties(String prefix) {
		return getProperties(prefix, System.getProperties());
	}
}