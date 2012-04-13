/**
 * 
 */
package stream.io;

import stream.data.Data;
import stream.service.Service;

/**
 * @author chris
 * 
 */
public interface QueueService extends Service {

	public Data poll();

	public void enqueue(Data item);
}
