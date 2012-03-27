/**
 * 
 */
package stream.tools;

import java.util.LinkedHashMap;
import java.util.Map;

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
	final Map<String, String> properties = new LinkedHashMap<String, String>();

	public ExperimentContext() {
		this("local");
	}

	public ExperimentContext(String name) {
		super(name);
		log.info("Creating experiment-context '{}'", name);
	}

	public Map<String, String> getProperties() {
		return properties;
	}
}
