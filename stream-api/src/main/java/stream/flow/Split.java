/**
 * 
 */
package stream.flow;

import java.util.List;

import stream.expressions.Condition;
import stream.io.Sink;

/**
 * <p>
 * This interface defines an abstract split element. A split element is
 * essentially just like a queue, but allows for multiple consumers to connect
 * with a given condition.
 * </p>
 * <p>
 * Each consumer will then receive the items matching the condition it provided.
 * The exact behavior is left to the different implementations of {@link Split}.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 * 
 */
public interface Split extends Sink {

	/**
	 * List the conditions registered at this split point.
	 * 
	 * @return
	 */
	public List<Condition> getConditions();

	/**
	 * Adds a new sink with a given condition to the split.
	 * 
	 * @param condition
	 * @param sink
	 */
	public void add(Condition condition, Sink sink);
}
