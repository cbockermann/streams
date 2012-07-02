/**
 * 
 */
package stream.runtime;

/**
 * @author chris
 * 
 */
public interface LifeCycle {

	public void init() throws Exception;

	public void finish() throws Exception;
}
