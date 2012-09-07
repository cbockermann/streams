/**
 * 
 */
package stream.classifier;

import java.io.Serializable;
import java.rmi.RemoteException;

import stream.AbstractProcessor;
import stream.annotations.Parameter;
import stream.data.Data;
import stream.learner.PredictionService;

/**
 * <p>
 * This class implements an abstract classifier, i.e. an instance that is
 * capable of learning from observations of type Serializable (may be numbers or
 * Strings).
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 * 
 */
public abstract class AbstractClassifier extends AbstractProcessor implements
		PredictionService, Serializable {

	/** The unique class ID */
	private static final long serialVersionUID = -6260664658067095723L;

	protected String id = null;
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
	@Parameter(description = "The label attribute to use")
	public void setLabel(String label) {
		this.label = label;
	}

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
	 * @see stream.learner.PredictionService#getName()
	 */
	@Override
	public String getName() throws RemoteException {
		if (id != null)
			return id;
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

	public abstract void reset() throws Exception;

	public abstract void train(Data item);
}
