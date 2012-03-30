package stream.eval;

import java.util.LinkedHashMap;
import java.util.Map;

import stream.data.AbstractDataProcessor;
import stream.data.Data;
import stream.learner.Learner;

public class TrainProcessor extends AbstractDataProcessor {

	Map<String, Learner<?>> learners = new LinkedHashMap<String, Learner<?>>();

	public void addLearner(String name, Learner<?> learner) {
		learners.put(name, learner);
	}

	public Learner<?> removeLearner(String name) {
		return learners.remove(name);
	}

	/**
	 * @see stream.data.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data data) {

		for (String key : learners.keySet()) {
			learners.get(key).learn(data);
		}
		return data;
	}
}