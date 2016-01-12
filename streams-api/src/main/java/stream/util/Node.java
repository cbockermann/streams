/**
 * 
 */
package stream.util;

import java.util.Map;

/**
 * @author chris
 * 
 */
public interface Node {

	public Node set(String key, String value);

	public Map<String, String> attributes();
}
