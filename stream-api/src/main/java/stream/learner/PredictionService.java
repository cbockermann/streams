/**
 * 
 */
package stream.learner;

import java.io.Serializable;

import stream.data.Data;
import stream.service.Service;

/**
 * <p>
 * A stream/online learning algorithm provides any-time prediction capabilities.
 * This interface should be implemented by all learning processors that provide
 * classification/prediction.
 * </p>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public interface PredictionService extends Service {

	/**
	 * Returns the name of the prediction (i.e. the name of the label attribute)
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * Performs the prediction based on the current state of the implementing
	 * class (learning algorithm).
	 * 
	 * @param item
	 *            The data item (vector) that holds the unlabeled data.
	 * @return The label value for that data (prediction).
	 */
	public Serializable predict(Data item);
}
