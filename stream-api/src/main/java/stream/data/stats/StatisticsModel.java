/**
 * 
 */
package stream.data.stats;

import stream.data.Data;
import stream.learner.Model;

/**
 * @author chris
 * 
 */
public class StatisticsModel implements Model {

	/** The unique class ID */
	private static final long serialVersionUID = 3497165852628945575L;

	String name;
	Statistics statistics;

	public StatisticsModel(String name, Statistics statistics) {
		this.name = name;
		this.statistics = new Statistics(statistics);
	}

	/**
	 * @see stream.learner.Model#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * @see stream.learner.Model#process(stream.data.Data)
	 */
	@Override
	public Data process(Data item) {
		if (statistics != null) {
			for (String key : statistics.keySet()) {
				item.put("@" + getName() + ":" + key, statistics.get(key));
			}
		}
		return item;
	}
}
