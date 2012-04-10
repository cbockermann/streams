/**
 * 
 */
package stream.learner;

import java.io.Serializable;

import stream.data.AbstractDataProcessor;
import stream.data.Data;

/**
 * @author chris
 * 
 */
public abstract class AbstractRegressor extends AbstractDataProcessor implements
		Regressor {

	/** The unique class ID */
	private static final long serialVersionUID = 951585509815153514L;

	String id;

	/**
	 * @see stream.model.PredictionModel#predict(java.lang.Object)
	 */
	@Override
	public abstract Double classify(Data item);

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @see stream.data.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data data) {
		learn(data);
		return data;
	}

	/**
	 * @see stream.learner.Learner#reset()
	 */
	@Override
	public void reset() {
	}

	/**
	 * @see stream.learner.PredictionService#predict(stream.data.Data)
	 */
	@Override
	public Serializable predict(Data item) {
		return classify(item);
	}
}