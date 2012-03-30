/**
 * 
 */
package stream.learner.evaluation;

import java.io.Serializable;

import stream.data.AbstractDataProcessor;
import stream.data.Data;
import stream.util.ParameterUtils;

/**
 * @author chris
 * 
 */
public class PredictionError extends AbstractDataProcessor {

	final LossFunction<Serializable> loss = new ZeroOneLoss<Serializable>();
	String prefix = "@error:";
	String label;
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

		for (String classifier : learner) {
			Serializable pred = data.get("@prediction:" + classifier);
			if (pred != null) {
				Double error = loss.loss(labelValue, pred);
				data.put(prefix + classifier, error);
			}
		}

		return data;
	}
}
