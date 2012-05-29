/**
 * 
 */
package stream.classifier;

import java.io.Serializable;
import java.rmi.RemoteException;

import stream.AbstractProcessor;
import stream.data.Data;
import stream.learner.PredictionService;

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
public abstract class AbstractClassifier extends AbstractProcessor implements
		PredictionService, Serializable {

	/** The unique class ID */
	private static final long serialVersionUID = -6260664658067095723L;

	protected String label = null;

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label
	 *            the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @see stream.learner.PredictionService#getName()
	 */
	@Override
	public String getName() throws RemoteException {
		return getClass().getCanonicalName();
	}

	/**
	 * @see stream.Processor#process(stream.data.Data)
	 */
	public Data process(Data item) {

		if (label == null) {
			for (String key : item.keySet()) {
				if (key.startsWith("@label")) {
					label = key;
					break;
				}
			}
		}

		if (label != null && item.containsKey(label)) {
			train(item);
		}

		return item;
	}

	public abstract void train(Data item);
}
