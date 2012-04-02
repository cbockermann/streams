package stream.quantiles;

import stream.data.AbstractDataProcessor;
import stream.data.Data;

/**
 * Any deterministic quantile estimator should extend this class. It implements
 * {@link QuantileEstimator} and provides an appropriate constructor for general
 * deterministic quantile estimators.
 * 
 * @author Markus Kokott, Carsten Przyluczky
 * @see QuantileEstimator
 */

public abstract class DeterministicQuantileEstimator extends
		AbstractDataProcessor implements QuantileLearner {

	private static final long serialVersionUID = 5886919177952931056L;

	String key;

	/**
	 * This value specifies the error bound.
	 */
	protected double epsilon;

	/**
	 * This constructor spawns a deterministic quantile estimator with specified
	 * error bound.
	 * 
	 * @param epsilon
	 *            - an error parameter. <code>float</code> values between 0 and
	 *            1 are allowed.
	 * @throws RuntimeException
	 *             if epsilon doesn't fit into an error parameters codomain
	 */
	public DeterministicQuantileEstimator(double epsilon) {

		if (epsilon <= 0 || epsilon >= 1) {
			throw new RuntimeException(
					"An appropriate epsilon value must lay between 0 and 1.");
		}

		this.epsilon = epsilon;
	}

	/**
	 * Returns the error parameter.
	 * 
	 * @return <code>float</code> value of error parameter epsilon
	 */
	public double getEpsilon() {
		return epsilon;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key
	 *            the key to set
	 */
	public void setKey(String key) {
		this.key = key;
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
	 * @see stream.learner.Learner#learn(stream.data.Data)
	 */
	@Override
	public void learn(Data item) {
		try {
			Double d = new Double(item.get(key) + "");
			learn(d);
		} catch (Exception e) {
		}

	}

	public abstract void learn(Double value);

}
