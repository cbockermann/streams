/**
 * 
 */
package stream.data.graph;

import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.io.CsvStream;

/**
 * @author chris
 * 
 */
public class GraphProvider implements GraphService {

	static Logger log = LoggerFactory.getLogger(GraphProvider.class);
	Map<String, Set<String>> neighbors = new HashMap<String, Set<String>>();
	File file;

	/**
	 * @return the file
	 */
	public File getFile() {
		return file;
	}

	/**
	 * @param file
	 *            the file to set
	 */
	public void setFile(File file) {
		this.file = file;
	}

	/**
	 * @see stream.service.Service#reset()
	 */
	@Override
	public void reset() throws Exception {
		log.info("Reading graph from {}", file);
		CsvStream stream = new CsvStream(new FileInputStream(file));
		Data item = stream.readNext();
		while (item != null) {
			Serializable start = item.get("start");
			Serializable end = item.get("end");
			if (start != null && end != null) {
				addEdge(start.toString(), end.toString());
			} else {
				throw new Exception(
						"Failed to extract start/end from data item: " + item
								+ "!");
			}
			item = stream.readNext();
		}
		stream.close();
	}

	public void addEdge(String start, String end) {

		Set<String> neighs = neighbors.get(start);
		if (neighs == null) {
			neighs = new LinkedHashSet<String>();
			neighbors.put(start, neighs);
		}
		neighs.add(end);
	}

	/**
	 * @see stream.data.graph.GraphService#getNodes()
	 */
	@Override
	public Set<String> getNodes() {
		return neighbors.keySet();
	}

	/**
	 * @see stream.data.graph.GraphService#getNeighbors(java.lang.String)
	 */
	@Override
	public Set<String> getNeighbors(String node) {
		if (neighbors.containsKey(node))
			return neighbors.get(node);
		else
			return new HashSet<String>();
	}

}
