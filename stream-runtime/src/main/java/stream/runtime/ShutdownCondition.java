/**
 * 
 */
package stream.runtime;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.io.DataStream;

/**
 * @author chris
 * 
 */
public class ShutdownCondition {

	static Logger log = LoggerFactory.getLogger(ShutdownCondition.class);

	public boolean isMet(DependencyGraph graph) {

		if (graph.nodes.isEmpty())
			return true;

		List<Monitor> monitors = new ArrayList<Monitor>();
		int processes = 0;
		int monitorCount = 0;

		for (Object node : graph.nodes) {
			if (node instanceof Process && !(node instanceof Monitor)) {
				processes++;
			}

			if (node instanceof Monitor) {
				monitors.add((Monitor) node);
				monitorCount++;
			}
		}

		if (processes == 0) {
			log.debug("No more processes running...");
			for (Monitor m : monitors) {
				try {
					log.debug("Finishing monitor {}", m);
					m.finish();
					log.debug("Removing monitor {} from dependency graph", m);
					graph.remove(m);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		if (processes == 0 && monitorCount == 0) {
			log.debug("All monitors, all processes have finished!");
			return true;
		}

		for (Object node : graph.nodes) {
			if (node instanceof DataStream) {
				continue;
			}

			if (node instanceof Monitor) {
				continue;
			}

			/*
			 * if (node instanceof Processor && (!(node instanceof
			 * AbstractProcess))) {
			 * log.debug("Ignoring dependency-condition for processor '{}'",
			 * node); continue; }
			 */

			if (!graph.getSourcesFor(node).isEmpty()) {
				log.debug(
						"Found referenced node '{}' with {} references -> shutdown condition not met.",
						node, graph.getSourcesFor(node).size());
				log.debug("   references are: {}", graph.getSourcesFor(node));
				return false;
			}
		}

		log.debug("shutdown-condition fulfilled!");
		return true;
	}

	public void waitForCondition(DependencyGraph graph) {
		while (!isMet(graph)) {
			try {
				log.debug("shutdown-condition not met, waiting for changes in the dependency-graph...");
				synchronized (graph.lock) {
					graph.lock.wait();
				}
			} catch (Exception e) {
				log.error("Error while waiting for shutdown-condition: {}",
						e.getMessage());
				if (log.isDebugEnabled())
					e.printStackTrace();
			}
		}
	}
}
