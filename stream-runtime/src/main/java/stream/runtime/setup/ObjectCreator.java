/**
 * 
 */
package stream.runtime.setup;

import java.util.Map;

/**
 * @author chris
 * 
 */
public interface ObjectCreator {

	public String getNamespace();

	public Object create(String className, Map<String, String> parameters)
			throws Exception;
}
