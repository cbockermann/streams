/**
 * 
 */
package stream.learner;

import java.io.Serializable;
import java.util.Set;

import stream.data.Statistics;
import stream.service.Service;

/**
 * @author chris
 * 
 */
public interface MetaDataService extends Service {

	public Statistics getStatistics(String key);

	public Set<Serializable> getTopValues(String key);
}
