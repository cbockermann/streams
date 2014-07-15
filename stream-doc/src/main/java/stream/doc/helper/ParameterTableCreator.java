/*
 *  streams library
 *
 *  Copyright (C) 2011-2014 by Christian Bockermann, Hendrik Blom
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
package stream.doc.helper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.annotations.Parameter;
import stream.runtime.setup.ParameterDiscovery;
import stream.service.Service;

/**
 * @author chris
 * 
 */
public class ParameterTableCreator {

	static Logger log = LoggerFactory.getLogger(ParameterTableCreator.class);

	public static class ParameterInfo {
		final String name;
		final Class<?> type;
		final String description;
		final boolean required;
		final Double min;
		final Double max;
		final String defaultValue;

		public ParameterInfo(Parameter p, Class<?> cl) {
			name = p.name();
			type = cl;
			defaultValue = p.defaultValue();
			description = p.description();
			min = p.min();
			max = p.max();
			required = p.required();
		}

		public ParameterInfo(String name, Class<?> type, String desc) {
			this.name = name;
			this.type = type;
			this.description = desc;
			this.min = null;
			this.max = null;
			this.required = true;
			this.defaultValue = "";
		}
	}

	public static List<ParameterInfo> getParameterInfos(Class<?> clazz) {
		List<ParameterInfo> infos = new ArrayList<ParameterInfo>();

		Map<String, Class<?>> types = ParameterDiscovery
				.discoverParameters(clazz);
		for (String key : types.keySet()) {
			Parameter p = ParameterDiscovery.getParameterAnnotation(clazz, key);
			if (p != null) {
				Class<?> type = ParameterDiscovery.getParameterType(clazz, key);
				infos.add(new ParameterTableCreator.ParameterInfo(p, type));
				continue;
			}

		}
		for (Method m : clazz.getMethods()) {

			if (m.getName().startsWith("set")
					&& m.getParameterTypes().length == 1) {

				Class<?> argType = m.getParameterTypes()[0];
				if (Service.class.isAssignableFrom(argType)) {
					log.info("Found service-setter!");
					infos.add(new ParameterInfo(m.getName().substring(3)
							.toLowerCase()
							+ "-ref", argType, ""));
				}
			}
		}

		return infos;
	}

	public static void main(String[] args) {
		List<ParameterInfo> infos = getParameterInfos(Service.class);
		for (ParameterInfo info : infos) {
			System.out.println(info.name);
		}
	}
}
