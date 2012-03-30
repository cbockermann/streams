/**
 * 
 */
package stream.data.stats;

import stream.data.Data;

/**
 * @author chris
 * 
 */
public class Average extends Sum {

	Double count = 0.0d;

	/**
	 * @see stream.data.stats.Sum#updateStatistics(stream.data.Data)
	 */
	@Override
	public void updateStatistics(Data item) {
		count += 1.0d;

		for (String key : keys) {
			Double val = null;
			try {
				val = new Double(item.get(key) + "");
			} catch (Exception e) {
				val = null;
			}

			if (val != null) {
				statistics.add(key, val);
				item.put(key, statistics.get(key) / count);
			}
		}
	}
}
