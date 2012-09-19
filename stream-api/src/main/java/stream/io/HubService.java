/**
 * 
 */
package stream.io;

import stream.service.Service;

/**
 * @author chris
 * 
 */
public interface HubService extends Service {

	public void register(DataStreamListener listener) throws Exception;

	public void unregister(DataStreamListener listener) throws Exception;
}
