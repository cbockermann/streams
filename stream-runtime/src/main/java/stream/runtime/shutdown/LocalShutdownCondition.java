/**
 * 
 */
package stream.runtime.shutdown;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.ComputeGraph;
import stream.Process;
import stream.Processor;
import stream.io.Source;
import stream.runtime.AbstractProcess;
import stream.runtime.Monitor;

/**
 * @author chris
 * 
 */
public class LocalShutdownCondition extends AbstractShutdownCondition {

	static Logger log = LoggerFactory.getLogger(LocalShutdownCondition.class);

	/**
	 * @see stream.runtime.ShutdownCondition#isMet(stream.ComputeGraph)
	 */
	@Override
	public boolean isMet(ComputeGraph graph) {

		log.debug("Checking if shutdown condition is met...");
		synchronized (graph) {

			if (graph.nodes().isEmpty())
				return true;

			List<Monitor> monitors = new ArrayList<Monitor>();
			int processes = 0;
			int monitorCount = 0;

			for (Object node : graph.nodes()) {
				if (node instanceof Monitor) {
					monitors.add((Monitor) node);
					monitorCount++;
					continue;
				}

				if (node instanceof Process && !(node instanceof Monitor)) {
					processes++;
				}
			}

			if (processes == 0) {
				log.debug("No more processes running...");
				return true;
			}

			if (processes == 0 && monitorCount == 0) {
				log.debug("All monitors, all processes have finished!");
				return true;
			}

			int hangon = 0;

			// for (Object root : graph.getRootSources()) {
			// log.info("Root source: {}", root);
			// }

			for (Object node : graph.nodes()) {
				if (node instanceof Source) {
					continue;
				}

				if (node instanceof Monitor) {
					continue;
				}
				if (node instanceof Processor
						&& (!(node instanceof AbstractProcess))) {
					log.debug(
							"Ignoring dependency-condition for processor '{}'",
							node);
					// continue;
					hangon++;
				}

				/*
				 * if (!graph.getSourcesFor(node).isEmpty()) { //log.info( //
				 * "Found referenced node '{}' with {} references -> shutdown condition not met."
				 * , // node, graph.getSourcesFor(node).size());
				 * //log.info("   references are: {}",
				 * graph.getSourcesFor(node)); hangon++; }
				 */
			}
			if (hangon > 0)
				return false;

			log.debug("shutdown-condition fulfilled!");
			return true;
		}
	}

	public void waitForCondition(ComputeGraph graph) {
		synchronized (graph) {
			while (!isMet(graph)) {
				try {
					//
					log.debug("shutdown-condition not met, waiting for changes in the dependency-graph...");
					graph.wait(1000L);
				} catch (Exception e) {
					log.error("Error while waiting for shutdown-condition: {}",
							e.getMessage());
					if (log.isDebugEnabled())
						e.printStackTrace();
				}
			}
		}
	}
}
