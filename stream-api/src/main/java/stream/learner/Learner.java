package stream.learner;

import java.io.Serializable;

import stream.StatefulProcessor;
import stream.data.Data;

/**
 * This interface defines the learning step of a machine learning algorithm. All
 * learners must implement this class.
 * 
 * @author beckers, homburg, mueller, schulte
 * 
 */
public interface Learner<M extends Model> extends Serializable,
		StatefulProcessor, ModelProvider<M> {

	/**
	 * Starts or continues to train a model.
	 * 
	 * @param item
	 *            The input for the learning process
	 */
	public void learn(Data item);
}