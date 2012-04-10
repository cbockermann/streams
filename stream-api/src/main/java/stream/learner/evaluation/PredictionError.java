/**
 * 
 */
package stream.learner.evaluation;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import stream.data.AbstractDataProcessor;
import stream.data.Data;
import stream.runtime.annotations.Description;
import stream.runtime.setup.ParameterUtils;

/**
 * <p>
 * This class implements a generic prediction error evaluator. The prediction
 * error(s) are added to the data item...
 * </p>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
@Description(name = "PredictionError", group = "Data Stream.Mining.Evaluation")
public class PredictionError extends AbstractDataProcessor {

	LossFunction<Serializable> loss = new ZeroOneLoss<Serializable>();
	String prefix = "@error:";
	String label = "@label";
	String[] learner;

	/**
	 * @return the prefix
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * @param prefix
	 *            the prefix to set
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	/**
	 * @return the labels
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param labels
	 *            the labels to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @return the learners
	 */
	public String getLearner() {
		return ParameterUtils.join(learner);
	}

	/**
	 * @param learners
	 *            the learners to set
	 */
	public void setLearner(String learner) {
		this.learner = ParameterUtils.split(learner);
	}

	/**
	 * @see stream.data.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data data) {

		Serializable labelValue = data.get(label);
		if (labelValue == null)
			return data;

		Map<String, Double> errors = new LinkedHashMap<String, Double>();

		//
		// if the user specified the learner names, we only check these...
		//
		if (learner != null) {
			for (String classifier : learner) {
				String key = Data.PREDICTION_PREFIX + ":" + classifier;
				Serializable pred = data.get(key);
				if (pred != null) {
					Double error = loss.loss(labelValue, pred);
					errors.put(prefix + classifier, error);
				}
			}
		} else {
			//
			// if no learner names/refs have been specified, we compute
			// prediction errors for all predictions in the data item
			//
			for (String key : data.keySet()) {
				if (key.startsWith(Data.PREDICTION_PREFIX)) {
					Serializable pred = data.get(key);
					String errKey = key.replaceFirst(Data.PREDICTION_PREFIX,
							prefix);

					Double error = loss.loss(labelValue, pred);
					errors.put(errKey, error);
				}
			}
		}

		for (String err : errors.keySet())
			data.put(err, errors.get(err));

		return data;
	}
}