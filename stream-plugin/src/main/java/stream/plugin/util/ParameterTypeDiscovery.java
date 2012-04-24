/*
 *  stream.ai
 *
 *  Copyright (C) 2011-2012 by Christian Bockermann, Hendrik Blom
 * 
 *  stream.ai is a library, API and runtime environment for processing high
 *  volume data streams. It is composed of three submodules "stream-api",
 *  "stream-core" and "stream-runtime".
 *
 *  The stream.ai library (and its submodules) is free software: you can 
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
package stream.plugin.util;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.annotations.EmbeddedContent;
import stream.annotations.Parameter;
import stream.expressions.Condition;
import stream.runtime.VariableContext;
import stream.runtime.setup.ParameterDiscovery;
import stream.runtime.setup.ParameterInjection;

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
 * This class implements an annotation-based parameter-type discovery. This
 * allows for annotating class fields and automatically adding these fields to
 * the RapidMiner operator object.
 * 
 * @author Christian Bockermann
 * 
 */
public class ParameterTypeDiscovery {

	/* The global logger for this class */
	static Logger log = LoggerFactory.getLogger(ParameterTypeDiscovery.class);

	/**
	 * Check the given class for any @parameter annotated fields.
	 * 
	 * @param clazz
	 * @return
	 */
	public static Map<String, ParameterType> discoverParameterTypes(
			Class<?> clazz) {

		log.debug("------------------------------------------------------------------------");
		log.debug("Exploring ParameterTypes for class '{}'", clazz);
		Map<String, ParameterType> types = new LinkedHashMap<String, ParameterType>();

		for (Method m : clazz.getMethods()) {

			log.trace("Checking method {}", m);

			if (ParameterDiscovery.isSetter(m)) {
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

				Parameter param = m.getAnnotation(Parameter.class);
				if (param == null) {
					log.debug(
							"Method '{}' is not annotated as Parameter -> skipping",
							m.getName());
					continue;
				}

				if (param != null && !"".equals(param.name().trim())) {
					key = param.name();
					log.debug("Setting parameter for method '{}' to key '{}'",
							m.getName(), key);
				}

				Class<?>[] t = m.getParameterTypes();
				if (t[0] == EmbeddedContent.class) {
					log.debug("Found EmbeddedContent parameter, key = '{}'",
							key);
					ParameterType type = new ParameterTypeText(key, "",
							TextType.JAVA);
					types.put(key, type);
					continue;
				}

				ParameterType type = getParameterType(param, key,
						m.getParameterTypes()[0]);
				if (type != null) {
					log.debug("Adding parameter-type: {}", type);
					types.put(key, type);
					log.debug("  => parameter '{}'", key);
				}
			}
		}

		if (log.isDebugEnabled()) {

			for (String key : types.keySet()) {
				ParameterType type = types.get(key);
				log.debug("  key '{}' => {}  (name: " + type.getKey() + ")",
						key, type);
			}
		}

		log.debug("------------------------------------------------------------------------");
		return types;
	}

	public static ParameterType getParameterType(Parameter param, String name,
			Class<?> type) {

		String desc = "";
		ParameterType pt = null;

		String key = name;
		if (param != null && param.name() != null
				&& !"".equals(param.name().trim())) {
			key = param.name();
		}

		if (param != null && param.description() != null) {
			desc = param.description();
		}

		if (type.equals(String.class)
				|| type.equals(Condition.class)
				|| (type.isArray() && type.getComponentType().equals(
						String.class))) {
			log.debug("ParameterType is a String");

			if (param != null && param.values() != null) {
				pt = new ParameterTypeString(key, desc, !param.required());
			} else {
				pt = new ParameterTypeString(key, desc, false);
			}
			if (param != null && param.defaultValue() != null)
				pt.setDefaultValue(param.defaultValue());

			if (param != null && param.values() != null
					&& param.values().length > 1) {
				log.debug("Found category-parameter!");
				ParameterTypeCategory cat = new ParameterTypeCategory(key,
						desc, param.values(), 0);
				return cat;
			}

			return pt;
		}

		if (type.equals(Double.class) || type.equals(double.class)) {
			log.debug("ParameterType {} is a Double!");
			if (param != null) {
				pt = new ParameterTypeDouble(key, desc, param.min(),
						param.max(), !param.required());
			} else
				pt = new ParameterTypeDouble(key, desc, Double.MIN_VALUE,
						Double.MAX_VALUE, 0.0d);
			if (param != null && param.defaultValue() != null) {
				pt.setDefaultValue(new Double(param.defaultValue()));
			}
			return pt;
		}

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

			if (param != null) {
				pt = new ParameterTypeInt(key, desc, min, max,
						!param.required());
			} else {
				pt = new ParameterTypeInt(key, desc, 0, Integer.MAX_VALUE, true);
			}

			if (param != null && param.defaultValue() != null) {
				try {
					pt.setDefaultValue(new Integer(param.defaultValue()));
				} catch (Exception e) {
					log.error("Failed to determine default-value: {}",
							e.getMessage());
					pt.setDefaultValue(0);
				}
			}
			return pt;
		}

		if (type.equals(Boolean.class) || type.equals(boolean.class)) {
			log.debug("ParameterType {} is a Boolean!");
			if (param != null)
				pt = new ParameterTypeBoolean(key, desc, !param.required());
			else
				pt = new ParameterTypeBoolean(key, desc, true);
			if (param != null && param.defaultValue() != null) {
				pt.setDefaultValue(new Boolean(param.defaultValue()));
			}
			return pt;
		}

		if (type.equals(File.class)) {
			pt = new ParameterTypeFile(key, desc, null, !param.required());
			return pt;
		}

		if (Map.class.isAssignableFrom(type)) {

			log.debug("Found Map parameter... ");
			pt = new ParameterTypeList(key, desc, new ParameterTypeString(
					"key", ""), new ParameterTypeString("value", ""));
			return pt;
		}

		return pt;
	}

	public static void inject(Object object, Map<String, Object> parameters) {
		try {
			log.debug("Using ParameterInjection to set parameters...");
			ParameterInjection
					.inject(object, parameters, new VariableContext());
		} catch (Exception e) {
			log.error("Failed to inject parameters: {}", e.getMessage());
			if (log.isDebugEnabled())
				e.printStackTrace();
		}
	}

	public static List<ParameterType> getParameterTypes(Class<?> clazz) {
		Map<String, ParameterType> types = discoverParameterTypes(clazz);
		List<ParameterType> list = new ArrayList<ParameterType>();
		for (String name : types.keySet()) {
			list.add(types.get(name));
		}
		return list;
	}
}