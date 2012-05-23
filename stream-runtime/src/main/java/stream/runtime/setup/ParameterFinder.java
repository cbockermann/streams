/**
 * 
 */
package stream.runtime.setup;

import java.util.Map;
import java.util.Set;

/**
 * <p>
 * This interface defines a single method for finding parameters given a class.
 * The implementing classes may use annotations or conventions to search the
 * class for parameters.
 * </p>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public interface ParameterFinder {

	/**
	 * Returns a map of parameters (name,type) for the specified class.
	 * 
	 * @param clazz
	 * @return
	 */
	public Map<String, Class<?>> findParameters(Class<?> clazz);

	/**
	 * This method injects the given parameters into the specified object, based
	 * on the map returned by the {{@link #findParameters(Class)} method.
	 * 
	 * @param params
	 * @param o
	 * @return The parameter names that have been injected.
	 */
	public Set<String> inject(Map<String, ?> params, Object o) throws Exception;
}
