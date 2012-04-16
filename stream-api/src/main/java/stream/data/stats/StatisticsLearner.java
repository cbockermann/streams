/**
 * 
 */
package stream.data.stats;

import stream.AbstractProcessor;
import stream.data.Data;

/**
 * @author chris
 * 
 */
public abstract class StatisticsLearner extends AbstractProcessor implements
		StatisticsService {

	String[] keys;
	Statistics statistics = new Statistics();

	/**
	 * @return the keys
	 */
	public String[] getKeys() {
		return keys;
	}

	/**
	 * @param keys
	 *            the keys to set
	 */
	public void setKeys(String[] keys) {
		this.keys = keys;
	}

	/**
	 * @see stream.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data data) {
		updateStatistics(data);
		return data;
	}

	/**
	 * @see stream.data.stats.StatisticsService#getStatistics(java.lang.String)
	 */
	@Override
	public Statistics getStatistics() {
		return new Statistics(statistics);
	}

	public abstract void updateStatistics(Data item);

	/**
	 * @see stream.service.Service#reset()
	 */
	@Override
	public void reset() throws Exception {
		this.resetState();
	}

	/**
	 * @see stream.AbstractProcessor#resetState()
	 */
	@Override
	public void resetState() throws Exception {
		this.statistics = new Statistics();
	}

}