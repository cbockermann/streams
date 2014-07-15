/**
 * 
 */
package stream.runtime.shutdown;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.app.ComputeGraph;

/**
 * @author hendrik
 * 
 */
public class ServerShutdownCondition extends AbstractShutdownCondition {

	static Logger log = LoggerFactory.getLogger(ServerShutdownCondition.class);

	/**
	 * @see stream.runtime.ShutdownCondition#isMet(stream.app.ComputeGraph)
	 */
	@Override
	public boolean isMet(ComputeGraph graph) {
		return false;
	}
}
