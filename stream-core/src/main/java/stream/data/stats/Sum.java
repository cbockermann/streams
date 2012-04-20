/**
 * 
 */
package stream.data.stats;

import stream.data.Data;
import stream.data.StatisticsLearner;

/**
 * @author chris
 * 
 */
public class Sum extends StatisticsLearner {

	/**
	 * @see stream.data.StatisticsLearner#updateStatistics(stream.data.Data)
	 */
	@Override
	public void updateStatistics(Data item) {

		for (String key : keys) {
			Double val = null;
			try {
				val = new Double(item.get(key) + "");
			} catch (Exception e) {
				val = null;
			}

			if (val != null) {
				statistics.add(key, val);
				item.put(key, statistics.get(key));
			}
		}
	}
}