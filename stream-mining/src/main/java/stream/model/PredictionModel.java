package stream.model;

import java.io.Serializable;

import stream.data.Data;

/**
 * <p>
 * An output extension to the model interface which returns a prediction for a
 * given input. E.g. for clustering, regression, classification etcpp.
 * </p>
 * 
 * @author beckers, homburg, mueller, schulte, skirzynski
 * 
 */
public abstract class PredictionModel<R extends Serializable> extends
		AbstractModel {

	/** The unique class ID */
	private static final long serialVersionUID = 8409479853926260311L;

	/**
	 * @param name
	 */
	public PredictionModel(String name) {
		super(name);
	}

	/**
	 * <p>
	 * This method returns a prediction for the given input.
	 * </p>
	 * 
	 * @param item
	 *            to predict for
	 * @return a prediction for the given item
	 */
	public abstract R predict(Data item);

	/**
	 * @see stream.learner.Model#process(stream.data.Data)
	 */
	@Override
	public Data process(Data item) {
		R pred = predict(item);
		item.put("@prediction:" + getName(), pred);
		return item;
	}
}