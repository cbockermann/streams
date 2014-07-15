package stream.runtime.shutdown;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.app.ComputeGraph;

/**
 * @author hendrik
 * 
 */
public abstract class AbstractShutdownCondition implements ShutdownCondition {

	static Logger log = LoggerFactory
			.getLogger(AbstractShutdownCondition.class);

	/**
	 * @see stream.runtime.ShutdownCondition#waitForCondition(stream.runtime.
	 *      DependencyGraph)
	 */
	public void waitForCondition(ComputeGraph graph) {
		synchronized (graph) {
			while (!isMet(graph)) {
				try {
					//
					log.debug("shutdown-condition not met, waiting for changes in the dependency-graph...");
					graph.wait();
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
