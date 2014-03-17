/**
 * 
 */
package stream.doc.helper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.annotations.Parameter;
import stream.io.Sink;
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
							.toLowerCase(), argType, ""));
					continue;
				}

				if (Sink.class.isAssignableFrom(argType)) {
					log.info("Found sink setter!");
					infos.add(new ParameterInfo(m.getName().substring(3)
							.toLowerCase(), argType, ""));
					continue;
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
