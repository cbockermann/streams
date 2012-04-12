/**
 * 
 */
package stream.data.stats;

import stream.AbstractDataProcessor;
import stream.data.Data;
import stream.learner.ModelProvider;

/**
 * @author chris
 * 
 */
public abstract class StatisticsLearner extends AbstractDataProcessor implements
		ModelProvider<StatisticsModel> {

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
	 * @see stream.learner.ModelProvider#getModel()
	 */
	@Override
	public StatisticsModel getModel() {
		return new StatisticsModel(getId(), statistics);
	}

	public abstract void updateStatistics(Data item);
}