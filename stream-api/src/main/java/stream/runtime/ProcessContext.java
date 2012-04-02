/**
 * 
 */
package stream.runtime;

/**
 * @author chris
 * 
 */
public interface ProcessContext extends Context {

	public Object get(String key);

	public void set(String key, Object o);
}
