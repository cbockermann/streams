/**
 * 
 */
package stream.runtime.shutdown;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * stream.runtime.ShutdownCondition#isMet(stream.runtime.DependencyGraph)
	 */
	@Override
	public boolean isMet(DependencyGraph graph) {

		synchronized (graph) {

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
						log.debug("Removing monitor {} from dependency graph",
								m);
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

			int hangon = 0;

			for (Object root : graph.getRootSources()) {
				// log.info("Root source: {}", root);
			}

			for (Object node : graph.nodes) {
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

	public void waitForCondition(DependencyGraph graph) {
		synchronized (graph) {
			while (!isMet(graph)) {
				try {
					// log.info("shutdown-condition not met, waiting for changes in the dependency-graph...");
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
