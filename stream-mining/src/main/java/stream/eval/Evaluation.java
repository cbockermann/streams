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
public interface Evaluation<L extends Learner<?>> {

	public abstract L getBaselineLearner();

	public abstract Map<String, L> getLearnerCollection();

	public abstract void addLearner(String name, L learnAlgorithm);

	public abstract Statistics test(Data data);

}