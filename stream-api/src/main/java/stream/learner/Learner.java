package stream.learner;

import java.io.Serializable;

import stream.data.Data;
import stream.data.DataProcessor;

/**
 * This interface defines the learning step of a machine learning algorithm. All
 * learners must implement this class.
 * 
 * @author beckers, homburg, mueller, schulte
 * 
 */
public interface Learner<M extends Model> extends Serializable, DataProcessor,
		ModelProvider<M> {

	/**
	 * This method is called after the learner has been created and all
	 * parameters have been set.
	 */
	public void reset();

	/**
	 * Starts or continues to train a model.
	 * 
	 * @param item
	 *            The input for the learning process
	 */
	public void learn(Data item);
}