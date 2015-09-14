/*
 *  streams library
 *
 *  Copyright (C) 2011-2014 by Christian Bockermann, Hendrik Blom
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
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.AbstractProcessor;
import stream.Data;
import stream.annotations.Description;

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

	static Logger log = LoggerFactory.getLogger(PredictionError.class);
	LossFunction<Serializable> loss = new ZeroOneLoss<Serializable>();
	String prefix = "@error";
	String label = "@label";
	String[] learner;
	Integer count = 0;
	Integer every = 0;

	protected Map<String, ConfusionMatrix<Serializable>> confusionMatrices = new LinkedHashMap<String, ConfusionMatrix<Serializable>>();

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
	 * @see stream.DataProcessor#process(stream.Data)
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

				ConfusionMatrix<Serializable> matrix = this.confusionMatrices
						.get(classifier);
				if (matrix == null) {
					matrix = new ConfusionMatrix<Serializable>();
					confusionMatrices.put(classifier, matrix);
				}

				matrix.add(labelValue, pred);
			}
		} else {
			//
			// if no learner names/refs have been specified, we compute
			// prediction errors for all predictions in the data item
			//
			for (String key : data.keySet()) {
				if (key.startsWith(Data.PREDICTION_PREFIX)) {
					Serializable pred = data.get(key);

					String name = key
							.substring(Data.PREDICTION_PREFIX.length());

					String errKey = key.replaceFirst(Data.PREDICTION_PREFIX,
							prefix);
					if (pred == null)
						continue;
					Double error = loss.loss(labelValue, pred);
					errors.put(errKey, error);

					ConfusionMatrix<Serializable> matrix = this.confusionMatrices
							.get(name);
					if (matrix == null) {
						matrix = new ConfusionMatrix<Serializable>();
						confusionMatrices.put(name, matrix);
					}

					matrix.add(labelValue, pred);
				}
			}
		}

		for (String err : errors.keySet()) {
			data.put(err, errors.get(err));
		}

		count++;
		if (every > 0 && count % every == 0) {
			for (String learner : confusionMatrices.keySet()) {
				log.info(confusionMatrices.get(learner).toString());
			}
		}

		return data;
	}

	/**
	 * @see stream.AbstractProcessor#finish()
	 */
	@Override
	public void finish() throws Exception {
		super.finish();

		for (String learner : confusionMatrices.keySet()) {
			log.info(confusionMatrices.get(learner).toString());
			StringBuffer config = new StringBuffer("\\begin{tabular}{p{2cm}");
			StringBuffer header = new StringBuffer("\\textbf{Label} ");
			StringBuffer body = new StringBuffer("Accuracy ");

			DecimalFormatSymbols dfs = new DecimalFormatSymbols();
			dfs.setDecimalSeparator('.');
			DecimalFormat fmt = new DecimalFormat("0.00", dfs);

			ConfusionMatrix<Serializable> matrix = confusionMatrices
					.get(learner);
			List<Serializable> labels = matrix.getLabels();
			for (Serializable label : labels) {
				config.append("|c");

				header.append("& \\textbf{" + label + "} ");
				TableOfConfusion conf = matrix.getTableOfConfusion(label);
				// double prec = conf.calculatePrecision();
				// double reca = conf.calculateRecall();
				double acc = conf.calculateAccuracy();

				body.append(" & " + fmt.format(acc) + " ");
			}
			config.append("|} \\\\ \\hline");
			header.append("\\\\ \\hline");
			body.append("\\\\ \\hline");
			body.append("\n");
			body.append("\\end{tabular}");

			System.out.println(config.toString());
			System.out.println(header.toString());
			System.out.println(body.toString());
		}

	}
}