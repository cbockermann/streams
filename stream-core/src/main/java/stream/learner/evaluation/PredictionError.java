/*
 *  streams library
 *
 *  Copyright (C) 2011-2012 by Christian Bockermann, Hendrik Blom
 * 
 *  streams is a library, API and runtime environment for processing high
 *  volume data streams. It is composed of three submodules "stream-api",
 *  "stream-core" and "stream-runtime".
 *
 *  The streams library (and its submodules) is free software: you can 
 *  redistribute it and/or modify it under the terms of the 
 *  GNU Affero General Public License as published by the Free Software 
 *  Foundation, either version 3 of the License, or (at your option) any 
 *  later version.
 *
 *  The stream.ai library (and its submodules) is distributed in the hope
 *  that it will be useful, but WITHOUT ANY WARRANTY; without even the implied 
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/.
 */
package stream.learner.evaluation;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import stream.AbstractProcessor;
import stream.annotations.Description;
import stream.data.Data;

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
public class PredictionError extends AbstractProcessor {

	LossFunction<Serializable> loss = new ZeroOneLoss<Serializable>();
	String prefix = "@error";
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
	public String[] getLearner() {
		return learner;
	}

	/**
	 * @param learners
	 *            the learners to set
	 */
	public void setLearner(String[] learner) {
		this.learner = learner;
	}

	/**
	 * @see stream.DataProcessor#process(stream.data.Data)
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
				// First Element
				if (pred == null)
					continue;

				Double error = loss.loss(labelValue, pred);
				errors.put(prefix + classifier, error);
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
					if (pred == null)
						continue;
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