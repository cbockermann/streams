package stream.quantiles.impl;

import stream.annotations.Parameter;

/**
 * Any deterministic quantile estimator should extend this class. It implements
 * {@link QuantileEstimator} and provides an appropriate constructor for general
 * deterministic quantile estimators.
 * 
 * @author Markus Kokott, Carsten Przyluczky, Christian Bockermann
 * @see QuantileEstimator
 */

public abstract class DeterministicQuantileEstimator extends
		AbstractQuantileLearner {
	/** This value specifies the error bound */
	protected Double epsilon;

	/**
	 * @return the epsilon
	 */
	public Double getEpsilon() {
		return epsilon;
	}

	/**
	 * @param epsilon
	 *            the epsilon to set
	 */
	@Parameter(min = 0.0, max = 0.0, required = true, description = "The maximum error rate")
	public void setEpsilon(Double epsilon) {
		this.epsilon = epsilon;
	}
}
