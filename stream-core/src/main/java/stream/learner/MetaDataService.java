/**
 * 
 */
package stream.learner;

import java.io.Serializable;
import java.util.Set;

import stream.Service;
import stream.data.stats.Statistics;

/**
 * @author chris
 * 
 */
public interface MetaDataService extends Service {

	public Statistics getStatistics(String key);

	public Set<Serializable> getTopValues(String key);
}
