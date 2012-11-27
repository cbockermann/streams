/**
 * 
 */
package stream.expressions;

/**
 * <p>
 * This interface is provided by any implementation that can be queried for a
 * condition.
 * </p>
 * 
 * @author Hendrik Blom, Christian Bockermann
 * 
 */
public interface HasCondition {

	/**
	 * The condition provided by this instance.
	 * 
	 * @return The condition provided by this instance.
	 */
	public Condition getCondition();
}
