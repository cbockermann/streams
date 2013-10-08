/**
 * 
 */
package stream.runtime.setup;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import stream.util.Variables;

/**
 * @author chris
 * 
 */
public class DefaultParameterFinder implements ParameterFinder {

	final static List<ParameterFinder> finders = new ArrayList<ParameterFinder>();

	/**
	 * @see stream.runtime.setup.ParameterFinder#findParameters(java.lang.Class)
	 */
	@Override
	public Map<String, Class<?>> findParameters(Class<?> clazz) {
		Map<String, Class<?>> params = ParameterDiscovery
				.discoverParameters(clazz);
		for (ParameterFinder finder : finders) {
			params.putAll(finder.findParameters(clazz));
		}
		return params;
	}

	/**
	 * @see stream.runtime.setup.ParameterFinder#inject(java.util.Map,
	 *      java.lang.Object)
	 */
	@Override
	public Set<String> inject(Map<String, ?> params, Object o) throws Exception {
		Set<String> set = new LinkedHashSet<String>();
		set.addAll(ParameterInjection.inject(o, params, new Variables()));

		for (ParameterFinder finder : finders) {
			set.addAll(finder.inject(params, o));
		}
		return set;
	}
}
