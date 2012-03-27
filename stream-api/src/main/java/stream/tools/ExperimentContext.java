/**
 * 
 */
package stream.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Context;
import stream.logic.DefaultLookupService;

/**
 * @author chris
 * 
 */
public class ExperimentContext extends DefaultLookupService implements Context {

	static Logger log = LoggerFactory.getLogger(ExperimentContext.class);

	public ExperimentContext() {
		this("local");
	}

	public ExperimentContext(String name) {
		super(name);
		log.info("Creating experiment-context '{}'", name);
	}
}
