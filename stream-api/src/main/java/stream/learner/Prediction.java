/**
 * 
 */
package stream.learner;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.AbstractDataProcessor;
import stream.Processor;
import stream.data.Data;
import stream.runtime.Context;
import stream.runtime.annotations.Description;

/**
 * @author chris
 * 
 */
@Description(name = "Prediction", group = "Data Stream.Mining")
public class Prediction extends AbstractDataProcessor {

	static Logger log = LoggerFactory.getLogger(Prediction.class);

	String ref;

	PredictionService predictionService;

	/**
	 * @return the ref
	 */
	public String getRef() {
		return ref;
	}

	/**
	 * @param ref
	 *            the ref to set
	 */
	public void setRef(String ref) {
		this.ref = ref;
	}

	public void setLearner(PredictionService predService) {
		this.predictionService = predService;
	}

	/**
	 * @see stream.AbstractDataProcessor#init(stream.runtime.Context)
	 */
	@Override
	public void init(Context ctx) throws Exception {
		super.init(ctx);

		Object o = context.lookup(getRef());
		if (!(o instanceof ModelProvider)) {
			throw new Exception(
					"Referenced element '"
							+ getRef()
							+ "' does not provide a prediction model! (Implementation of ModelProvider expected!)");
		}
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
		}

		try {
			Processor p = context.lookup(ref);

			if (p instanceof ModelProvider) {
				ModelProvider<?> modelProvider = (ModelProvider<?>) p;
				log.debug("Found model-provider: {}", modelProvider);
				log.debug("Model is: {}", modelProvider.getModel());
				return modelProvider.getModel().process(data);
			} else {
				log.error(
						"Referenced element '{}' is not a learner/does not provide a model!",
						p);
			}

		} catch (Exception e) {
			log.error("Failed to look up: {}", e.getMessage());
		}

		return data;
	}
}
