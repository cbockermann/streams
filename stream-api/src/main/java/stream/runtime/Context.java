/**
 * 
 */
package stream.runtime;

/**
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public interface Context extends LookupService {

	public Object resolve(String variable);
}
