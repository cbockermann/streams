/**
 * 
 */
package stream.test;

import java.util.List;

import stream.Data;
import stream.service.Service;

/**
 * @author Christian Bockermann
 * 
 */
public interface CollectorService extends Service {

	public List<Data> getCollection();
}
