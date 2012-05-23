/**
 * 
 */
package stream.moa;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import moa.options.ClassOption;
import moa.options.FlagOption;
import moa.options.FloatOption;
import moa.options.IntOption;
import moa.options.MultiChoiceOption;
import moa.options.Option;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.runtime.setup.ParameterFinder;

/**
 * @author chris
 * 
 */
public class MoaParameterFinder implements ParameterFinder {

	static Logger log = LoggerFactory.getLogger(MoaParameterFinder.class);

	/* The index of available MOA processors */
	final MoaClassIndex index = new MoaClassIndex();

	final static MoaParameterFinder sharedInstance = new MoaParameterFinder();

	/**
	 * @see stream.runtime.setup.ParameterFinder#findParameters(java.lang.Class)
	 */
	@Override
	public Map<String, Class<?>> findParameters(Class<?> clazz) {

		try {
			Map<String, Class<?>> types = new LinkedHashMap<String, Class<?>>();

			Object o = clazz.newInstance();

			Field[] fields = clazz.getFields();
			for (Field f : fields) {
				String name = f.getName().replace("Option", "");

				if (!Option.class.isAssignableFrom(f.getType()))
					continue;

				Option option = (Option) f.get(o);
				name = option.getName();

				if (IntOption.class.equals(f.getType())) {
					log.debug("  Found int-option!");
					types.put(name, Integer.class);
					continue;
				}

				if (FloatOption.class.equals(f.getType())) {
					log.debug("  Found float-option!");
					types.put(name, Float.class);
					continue;
				}

				if (FlagOption.class.isAssignableFrom(f.getType())) {

					FlagOption opt = (FlagOption) f.get(o);
					name = opt.getName();

					log.debug("  Found flag-option '{}'!", name);
					log.debug("   descr: {}", opt.getPurpose());

					types.put(name, Boolean.class);
					continue;
				}

				if (ClassOption.class.equals(f.getType())) {
					log.debug("  Found class-option '{}'!", name);
					ClassOption co = (ClassOption) f.get(o);
					log.debug("     default-value: {}",
							co.getDefaultCLIString());
					Class<?> reqType = co.getRequiredType();
					log.debug("     required type: {}", reqType);

					log.debug("     possible classes: {}",
							index.getClassesOfType(reqType));

					types.put(name, reqType);
					continue;
				}

				if (MultiChoiceOption.class.equals(f.getType())) {
					log.debug("  Found multi-choice option!");
					MultiChoiceOption mo = (MultiChoiceOption) f.get(o);
					log.debug("    purpose: {}", mo.getPurpose());
					log.debug("    options: {}", mo.getOptionLabels());
					log.debug("value: {}", mo.getDefaultCLIString());
					types.put(name, String[].class);
					continue;
				}

				log.error("  Unsupported option: {}", f);
			}

			return types;
		} catch (Exception e) {
			log.error("Failed to determine parameters for class {}", clazz);
			throw new RuntimeException(e.getMessage());
		}
	}

	/**
	 * @see stream.runtime.setup.ParameterFinder#inject(java.util.Map,
	 *      java.lang.Object)
	 */
	@Override
	public Set<String> inject(Map<String, ?> params, Object o) throws Exception {
		Set<String> filled = new LinkedHashSet<String>();

		for (Field f : o.getClass().getFields()) {

			if (Option.class.isAssignableFrom(f.getType())) {

				Option opt = (Option) f.get(o);
				String name = opt.getName();
				Object value = params.get(name);
				if (value == null) {
					log.error(
							"No value specified for parameter '{}' found in class {}",
							name, o.getClass().getCanonicalName());
					filled.add(name);
				} else {
					opt.setValueViaCLIString(value.toString());
					log.debug("Value '{}' set for parameter '{}'", value, name);
				}
			}
		}

		return filled;
	}

	public static Map<String, Class<?>> findParams(Class<?> clazz) {
		return sharedInstance.findParameters(clazz);
	}

	public static Set<String> injectParams(Map<String, ?> params, Object o)
			throws Exception {
		return sharedInstance.inject(params, o);
	}
}
