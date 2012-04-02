/**
 * 
 */
package stream.learner;

/**
 * This is a simple "service-level" interface that is implemented by
 * processors/service-nodes that provide a model. A model can be a
 * prediction-model (any-time property) or a descriptive statistics object or
 * the like.
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public interface ModelProvider<M extends Model> {

	/**
	 * This method is implemented by classes providing any kind of model
	 * (prediction-model, ...).
	 * 
	 * @return
	 */
	public M getModel();
}
