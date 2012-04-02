package stream.eval;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.AbstractDataProcessor;
import stream.data.Data;
import stream.learner.Learner;
import stream.model.PredictionModel;

public class TestThenTrainProcessor extends AbstractDataProcessor {
	static Logger log = LoggerFactory.getLogger(TestThenTrainProcessor.class);

	List<Learner<PredictionModel<Serializable>>> learners = new ArrayList<Learner<PredictionModel<Serializable>>>();

	/**
	 * @see stream.data.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data data) {

		for (Learner<PredictionModel<Serializable>> learner : learners) {
			PredictionModel<Serializable> model = learner.getModel();
			Serializable prediction = model.predict(data);
			data.put("@prediction:" + model.getName(), prediction);
		}

		return data;
	}
}