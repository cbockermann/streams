/**
 * 
 */
package stream.runtime;

import stream.Context;

/**
 * <p>
 * The life cycle interface defines a general life cycle of elements within a
 * context. This is provided by elements that exists within the container of the
 * streams environment and which have a well defined life cycle.
 * </p>
 * 
 * @author Christian Bockermann, Hendrik Blom
 * 
 */
public interface LifeCycle {

	/**
	 * Initializes this life cycle instance with the provided context.
	 */
	public void init(Context context) throws Exception;

	/**
	 * This method is called at the end of the life cycle.
	 * 
	 * @throws Exception
	 */
	public void finish() throws Exception;
}
