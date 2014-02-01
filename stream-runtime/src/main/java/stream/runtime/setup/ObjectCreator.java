/**
 * 
 */
package stream.runtime.setup;

import java.util.Map;

import stream.util.Variables;

/**
 * @author chris
 * 
 */
public interface ObjectCreator {

	public String getNamespace();

	public Object create(String className, Map<String, String> parameters,
			Variables local) throws Exception;
}
