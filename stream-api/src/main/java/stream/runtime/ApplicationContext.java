/**
 * 
 */
package stream.runtime;

import stream.Context;
import stream.service.NamingService;

/**
 * An application context provides a general context and the functionality of a
 * naming service on top.
 * 
 * @author Christian Bockermann
 *
 */
public interface ApplicationContext extends Context, NamingService {

}
