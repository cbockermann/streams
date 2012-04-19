/**
 * 
 */
package stream.data.graph;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Processor;
import stream.data.Data;

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
	 * @see stream.Processor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data input) {
		log.info("Neighbors of {} are: {}", node,
				graphService.getNeighbors(node));
		return input;
	}
}
