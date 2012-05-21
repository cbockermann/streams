package stream.quantiles.impl;

import stream.StatefulProcessor;

/**
 * A quantile learner is a simple algorithm that observes a single attribute
 * denoted by the value of {{@link #getKey()}.
 * 
 * @author Christian Bockermann &lt;christian.bockermann &gt;
 * 
 */
public interface QuantileLearner extends StatefulProcessor {

	/**
	 * Returns the key/attribute the implementing instance is observing.
	 * 
	 * @return
	 */
	public String getKey();

	/**
	 * Sets the key/attribute the implementing instance shall observe.
	 * 
	 * @param key
	 */
	public void setKey(String key);

	/**
	 * Returns the <code>phi</code>-quantile of the observed attribute
	 * 
	 * @param phi
	 * @return
	 */
	public Double getQuantile(Double phi);

	public void learn(Double value);
}
