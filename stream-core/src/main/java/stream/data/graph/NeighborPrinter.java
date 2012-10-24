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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.Processor;

/**
 * @author chris
 * 
 */
public class NeighborPrinter implements Processor {

	static Logger log = LoggerFactory.getLogger(NeighborPrinter.class);
	GraphService graphService;

	String node;

	/**
	 * @return the graphService
	 */
	public GraphService getGraph() {
		return graphService;
	}

	/**
	 * @param graphService
	 *            the graphService to set
	 */
	public void setGraph(GraphService graphService) {
		this.graphService = graphService;
	}

	/**
	 * @return the node
	 */
	public String getNode() {
		return node;
	}

	/**
	 * @param node
	 *            the node to set
	 */
	public void setNode(String node) {
		this.node = node;
	}

	/**
	 * @see stream.Processor#process(stream.Data)
	 */
	@Override
	public Data process(Data input) {
		log.info("Neighbors of {} are: {}", node,
				graphService.getNeighbors(node));
		return input;
	}
}
