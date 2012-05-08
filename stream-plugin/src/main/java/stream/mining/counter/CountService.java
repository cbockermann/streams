/**
 * 
 */
package stream.mining.counter;

import java.io.Serializable;
import java.util.Set;

import stream.service.Service;

/**
 * @author chris
 * 
 */
public interface CountService extends Service {

	public Set<Serializable> getElements();

	public Long getCount(Serializable element);

}
