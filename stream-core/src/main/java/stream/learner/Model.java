package stream.learner;

import java.io.Serializable;

import stream.data.Data;

/**
 * <p>
 * This interface defines the basic structure of a model. All implementations of
 * specific models must implement this class.
 * </p>
 * 
 * <p>
 * For output methods implement {@link PredictionModel} or
 * {@link DescriptionModel}.
 * </p>
 * 
 * @author beckers, homburg, mueller, schulte, skirzynski
 * 
 */
public interface Model extends Serializable {

	public String getName();

	public Data process(Data item);
}