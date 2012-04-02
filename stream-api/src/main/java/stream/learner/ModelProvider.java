/**
 * 
 */
package stream.learner;


/**
 * @author chris
 * 
 */
public interface ModelProvider<M extends Model> {
	public M getModel();
}
