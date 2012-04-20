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
public class Minima extends StatisticsLearner {

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

			Double cur = statistics.get(key);
			if (val != null) {
				if (cur == null)
					statistics.put(key, val);
				else
					statistics.put(key, Math.max(cur, val));
			}
		}
	}
}