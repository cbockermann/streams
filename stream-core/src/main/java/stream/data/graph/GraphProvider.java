/*
 *  streams library
 *
 *  Copyright (C) 2011-2012 by Christian Bockermann, Hendrik Blom
 * 
 *  streams is a library, API and runtime environment for processing high
 *  volume data streams. It is composed of three submodules "stream-api",
 *  "stream-core" and "stream-runtime".
 *
 *  The streams library (and its submodules) is free software: you can 
 *  redistribute it and/or modify it under the terms of the 
 *  GNU Affero General Public License as published by the Free Software 
 *  Foundation, either version 3 of the License, or (at your option) any 
 *  later version.
 *
 *  The stream.ai library (and its submodules) is distributed in the hope
 *  that it will be useful, but WITHOUT ANY WARRANTY; without even the implied 
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/.
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

import stream.Data;
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
		log.debug("Reading graph from {}", file);
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
		log.debug("Adding edge ({},{})", start, end);
		log.debug("Neighbors of {} are: {}", start, neighs);
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
