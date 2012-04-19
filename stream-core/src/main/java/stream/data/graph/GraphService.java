/**
 * 
 */
package stream.data.graph;

import java.util.Set;

import stream.service.Service;

/**
 * @author chris
 * 
 */
public interface GraphService extends Service {

	public Set<String> getNodes();

	public Set<String> getNeighbors(String node);
}
