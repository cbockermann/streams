/**
 * 
 */
package stream.data;

import stream.service.Service;

/**
 * @author chris
 * 
 */
public interface CounterService extends Service {

	public Long getCount();
}
