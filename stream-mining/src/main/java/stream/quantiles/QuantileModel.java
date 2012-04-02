/**
 * 
 */
package stream.quantiles;

import stream.data.Data;
import stream.learner.Model;

/**
 * @author chris
 * 
 */
public class QuantileModel implements Model {

	/** The unique class ID */
	private static final long serialVersionUID = -2551643402177956274L;
	String name;

	public QuantileModel(String name) {
		this.name = name;
	}

	/**
	 * @see stream.learner.Model#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	public Double getQuantile(Double phi) {
		return Double.NaN;
	}

	/**
	 * @see stream.learner.Model#process(stream.data.Data)
	 */
	@Override
	public Data process(Data item) {
		return item;
	}
}