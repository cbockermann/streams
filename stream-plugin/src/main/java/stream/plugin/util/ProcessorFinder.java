/**
 * 
 */
package stream.plugin.util;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.annotations.Description;
import stream.plugin.GenericOperatorDescription;

/**
 * @author chris
 * 
 */
public class ProcessorFinder {

	static Logger log = LoggerFactory.getLogger(ProcessorFinder.class);

	public static List<Class<?>> findProcessors(String[] packageNames) {

		List<Class<?>> result = new ArrayList<Class<?>>();

		for (String pkgName : packageNames) {
			result.addAll(findProcessors(pkgName));
		}

		return result;
	}

	public static List<Class<?>> findProcessors(String pkgName) {
		List<Class<?>> list = new ArrayList<Class<?>>();

		try {
			Class<?>[] classes = ClassFinder.getClasses(pkgName);
			for (Class<?> clazz : classes) {

				if (clazz.isInterface()
						|| Modifier.isAbstract(clazz.getModifiers())) {
					continue;
				}

				if (clazz.isAnonymousClass()
						|| clazz.toString().indexOf("$") > 0) {
					continue;
				}

				if (!GenericOperatorDescription.canCreate(clazz))
					continue;

				Description desc = clazz.getAnnotation(Description.class);
				if (desc == null) {
					log.debug(
							"Skipping processor class '{}' due to missing Description annotation...",
							clazz);
					continue;
				}

				try {
					Class<?> pc = (Class<?>) clazz;
					list.add(pc);
				} catch (Exception e) {
					log.error(
							"Class does not extend the Processor interface: {}",
							clazz);
					if (log.isDebugEnabled())
						e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.debug("Found {} processors.", list.size());
		return list;
	}
}
