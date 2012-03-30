/**
 * 
 */
package stream.learner;

import java.io.Serializable;
import java.util.HashMap;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.AbstractDataProcessor;
import stream.data.Data;
import stream.data.vector.InputVector;
import stream.model.PredictionModel;

/**
 * <p>
 * This class implements an abstract classifier, i.e. an instance that is
 * capable of learning from observations of a specific, generic type
 * <code>D</code> and predicting class values of type <code>C</code>.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 * 
 * @param <D>
 *            The data type of the input examples used for learning and
 *            prediction.
 * @param <C>
 *            The label type, i.e. the Java class of the predicted outcome.
 * 
 */
public abstract class AbstractClassifier<D extends Serializable> extends
		AbstractDataProcessor implements Classifier<D> {
	/** The unique class ID */
	private static final long serialVersionUID = -8809157061575037435L;

	static Logger log = LoggerFactory.getLogger(AbstractClassifier.class);

	String id;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @see stream.learner.Learner#reset()
	 */
	public void reset() {
	}

	/**
	 * @see stream.learner.Learner#learn(java.lang.Object)
	 */
	@Override
	public abstract void learn(Data item);

	public static InputVector createSparseVector(Data datum) {
		if (datum.containsKey(".sparse-vector")) {
			log.trace("Found existing sparse-vector object!");
			return (InputVector) datum.get(".sparse-vector");
		}

		for (Serializable val : datum.values()) {
			if (val instanceof InputVector) {
				log.trace("Found existing sparse-vector object!");
				return (InputVector) val;
			}
		}

		TreeSet<String> indexes = new TreeSet<String>();
		for (String key : LearnerUtils.getAttributes(datum)) {
			Serializable val = datum.get(key);
			if (key.matches("\\d+") && val instanceof Double) {
				log.debug("Found numeric feature {}", key);
				indexes.add(key);
			} else {
				log.debug("Skipping non-numeric feature {} of type {}", key,
						val.getClass());
			}
		}

		double y = Double.NaN;
		if (datum.containsKey("@label")) {
			try {
				y = (Double) datum.get("@label");
			} catch (Exception e) {
				y = Double.NaN;
			}
		}

		// int[] idx = new int[ indexes.size() ];
		// double[] vals = new double[ indexes.size() ];
		HashMap<Integer, Double> pairs = new HashMap<Integer, Double>();

		// int i = 0;
		for (String key : indexes) {
			// idx[i] = Integer.parseInt( key );
			// vals[i] = (Double) datum.get( key );
			// i++;
			pairs.put((Integer) Integer.parseInt(key), (Double) datum.get(key));
		}

		// SparseVector vec = new SparseVector( idx, vals, y, false );
		InputVector vec = new InputVector(pairs, false, y);
		log.trace("SparseVector: {}", vec);
		return vec;
	}

	/**
	 * @see stream.data.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data data) {
		learn(data);
		return data;
	}

	/**
	 * @see stream.learner.Learner#getModel()
	 */
	@Override
	public PredictionModel<D> getModel() {
		return new PredictionModel<D>(getId()) {
			/** The unique class ID */
			private static final long serialVersionUID = -3230251564792978743L;

			@Override
			public Data process(Data item) {
				Serializable y = classify(item);
				if (y != null)
					item.put("@prediction:" + getId(), y);
				return item;
			}

			@Override
			public D predict(Data item) {
				return classify(item);
			}
		};
	}
}