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
package stream.learner;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.Processor;
import stream.annotations.Description;

/**
 * @author chris
 *
 */
@Description(name = "Prediction", group = "Data Stream.Mining")
public class Prediction implements Processor {

	static Logger log = LoggerFactory.getLogger(Prediction.class);

	PredictionService learner;

	public void setClassifier(PredictionService predService) {
		setLearner(predService);
	}

	public void setLearner(PredictionService predService) {
		log.info("Lerner injected: {}", predService);
		this.learner = predService;
	}

	/**
	 * @see stream.DataProcessor#process(stream.Data)
	 */
	@Override
	public Data process(Data data) {

		if (learner != null) {
			try {
				String key = learner.getName();
				Serializable pred = learner.predict(data);

				if (key != null && !key.startsWith(Data.PREDICTION_PREFIX)) {
					key = Data.PREDICTION_PREFIX + ":" + key;
				} else {
					key = "@prediction";
				}

				data.put(key, pred);
				return data;
			} catch (Exception e) {
				log.error("Failed to apply prediction: {}", e.getMessage());
				e.printStackTrace();
			}
		} else {
			log.error("No PredictionService has been injected!");
		}

		return data;
	}
}
