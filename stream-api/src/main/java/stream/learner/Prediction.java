/**
 * 
 */
package stream.learner;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Processor;
import stream.annotations.Description;
import stream.data.Data;

/**
 * @author chris
 * 
 */
@Description(name = "Prediction", group = "Data Stream.Mining")
public class Prediction implements Processor {

	static Logger log = LoggerFactory.getLogger(Prediction.class);

	PredictionService predictionService;

	public void setLearner(PredictionService predService) {
		this.predictionService = predService;
	}

	/**
	 * @see stream.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data data) {

		if (predictionService != null) {
			String key = predictionService.getName();
			Serializable pred = predictionService.predict(data);

			if (!key.startsWith(Data.PREDICTION_PREFIX)) {
				key = Data.PREDICTION_PREFIX + ":" + key;
			}

			data.put(key, pred);
			return data;
		} else {
			log.error("No PredictionService has been injected!");
		}

		return data;
	}
}
