/**
 * 
 */
package stream;

import stream.service.NamingService;

/**
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public interface Context extends NamingService {

	public Object resolve(String variable);
}
