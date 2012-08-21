/**
 * 
 */
package stream.data;

import java.io.Serializable;

import stream.Processor;

/**
 * @author chris
 * 
 */
public class NormalizeVector implements Processor {

	/*
	 * (non-Javadoc)
	 * 
	 * @see stream.Processor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data input) {

		Double sum = 0.0;
		for (String key : input.keySet()) {
			Serializable value = input.get(key);
			if (value instanceof Number) {
				double d = ((Number) value).doubleValue();
				sum += (d * d);
			}
		}

		if (sum > 0.0) {
			sum = Math.sqrt(sum);
			for (String key : input.keySet()) {
				Serializable value = input.get(key);
				if (value instanceof Number) {
					input.put(key, ((Number) value).doubleValue() / sum);
				}
			}
		}

		return input;
	}

}
