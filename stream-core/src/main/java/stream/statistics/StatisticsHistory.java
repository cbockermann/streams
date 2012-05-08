/**
 * 
 */
package stream.statistics;

import java.util.LinkedHashMap;

import stream.data.Statistics;

/**
 * @author chris
 * 
 */
public class StatisticsHistory extends History<Statistics> {

	/** The unique class ID */
	private static final long serialVersionUID = -2712326723596068372L;

	/**
	 * @param stepSize
	 * @param historyLength
	 */
	public StatisticsHistory(long stepSize, long historyLength) {
		super(stepSize, historyLength);
	}

	public StatisticsHistory(StatisticsHistory sh) {
		super(sh.stepSize, sh.historyLength);
		this.map = new LinkedHashMap<Long, Statistics>(sh.map);
	}

	/**
	 * @see stream.statistics.History#add(java.lang.Long, java.lang.Object)
	 */
	@Override
	public void add(Long timestamp, Statistics data) {

		Long x = adjust(timestamp);
		Statistics st = get(x);
		if (st == null) {
			st = new Statistics(data);
			this.map.put(x, st);
		} else {
			st.add(data);
		}
	}
}
