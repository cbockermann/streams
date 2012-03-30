/**
 * 
 */
package stream.learner;

import java.io.Serializable;

import stream.data.Data;
import stream.model.PredictionModel;

/**
 * <p>
 * A classifier is basically just a PredictionModel and the associated learning
 * algorithm. This interface defines that union.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 * 
 */
public interface Classifier<D extends Serializable> extends
		Learner<PredictionModel<D>> {

	public void setId(String id);

	public String getId();

	public D classify(Data item);
}
