/**
 * 
 */
package stream.quantiles.impl;

import java.io.Serializable;

import stream.AbstractProcessor;
import stream.annotations.Parameter;
import stream.data.Data;
import stream.quantiles.QuantilesService;

/**
 * @author chris
 * 
 */
public abstract class AbstractQuantileLearner extends AbstractProcessor
		implements QuantileLearner, QuantilesService {

	protected String key;

	/**
	 * @see stream.Processor#process(stream.data.Data)
	 */
	@Override
	public final Data process(Data input) {

		if (key != null) {
			Serializable val = input.get(key);
			if (val != null && Number.class.isAssignableFrom(val.getClass())) {
				Double value = ((Number) val).doubleValue();
				this.learn(value);
			}
		}

		return input;
	}

	/**
	 * @see stream.quantiles.impl.QuantileLearner#getKey()
	 */
	@Override
	public String getKey() {
		return this.key;
	}

	/**
	 * @see stream.quantiles.impl.QuantileLearner#setKey(java.lang.String)
	 */
	@Override
	@Parameter(required = true, description = "The key of the attribute to be observed by this estimator.")
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @see stream.quantiles.QuantilesService#getQuantileKeys()
	 */
	@Override
	public String[] getQuantileKeys() {
		if (key == null)
			return new String[0];

		return new String[] { key };
	}

	/**
	 * @see stream.quantiles.QuantilesService#getQuantile(java.lang.String,
	 *      java.lang.Double)
	 */
	@Override
	public Double getQuantile(String key, Double phi) {
		if (this.key == null || !this.key.equals(key))
			return Double.NaN;

		return getQuantile(phi);
	}

}
