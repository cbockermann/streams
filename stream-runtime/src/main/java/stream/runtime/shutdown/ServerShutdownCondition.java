/**
 * 
 */
package stream.runtime.shutdown;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author hendrik
 * 
 */
public class ServerShutdownCondition extends AbstractShutdownCondition {

	static Logger log = LoggerFactory.getLogger(ServerShutdownCondition.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * stream.runtime.ShutdownCondition#isMet(stream.runtime.DependencyGraph)
	 */
	@Override
	public boolean isMet(DependencyGraph graph) {
		return false;

	}

}
