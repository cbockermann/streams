/**
 * 
 */
package stream.quantiles;

import stream.data.AbstractDataProcessor;
import stream.data.Data;

/**
 * @author chris
 * 
 */
public abstract class AbstractQuantileLearner extends AbstractDataProcessor
		implements QuantileLearner {

	/** The unique class ID */
	private static final long serialVersionUID = -8371217254392571620L;

	String key;

	/**
	 * @return the feature
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param feature
	 *            the feature to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	public Double getValue(Data item) {

		if (key != null) {
			try {
				Double d = (Double) item.get(key);
				return d;
			} catch (Exception e) {
				return new Double("" + item.get(key));
			}
		}

		return null;
	}

	public Data process(Data item) {
		learn(item);
		return item;
	}

	public void learn(Data item) {
		Double value = getValue(item);
		if (value != null && !Double.isNaN(value)) {
			this.learn(value);
		}
	}

	public abstract void learn(Double value);
}
