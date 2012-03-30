/**
 * 
 */
package stream.eval;

import java.util.Map;

import stream.data.Data;
import stream.data.stats.Statistics;
import stream.learner.Learner;

/**
 * @author chris
 * 
 * @param <D>
 * @param <L>
 */
public interface Test<L extends Learner<?>> {

	/**
	 * @see stream.eval.Evaluation#test(D)
	 */
	public abstract Statistics test(Map<String, L> learner, Data data);

}