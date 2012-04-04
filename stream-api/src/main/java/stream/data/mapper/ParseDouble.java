/**
 * 
 */
package stream.data.mapper;

import stream.data.AbstractDataProcessor;
import stream.data.Data;
import stream.runtime.setup.ParameterUtils;

/**
 * @author chris
 * 
 */
public class ParseDouble extends AbstractDataProcessor {

	String[] keys = new String[0];

	/**
	 * @return the keys
	 */
	public String getKeys() {
		return ParameterUtils.join(keys);
	}

	/**
	 * @param keys
	 *            the keys to set
	 */
	public void setKeys(String keys) {
		this.keys = ParameterUtils.split(keys);
	}

	/**
	 * @see stream.data.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data data) {

		for (String key : keys) {
			Double value = Double.NaN;
			try {
				value = new Double(data.get(key) + "");
			} catch (Exception e) {
				value = Double.NaN;
			}
			data.put(key, value);
		}

		return data;
	}
}