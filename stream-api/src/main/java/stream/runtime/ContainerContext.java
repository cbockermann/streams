/**
 * 
 */
package stream.runtime;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chris
 * 
 */
public class ContainerContext extends DefaultLookupService implements Context {

	static Logger log = LoggerFactory.getLogger(ContainerContext.class);
	final Map<String, String> properties = new LinkedHashMap<String, String>();

	public ContainerContext() {
		this("local");
	}

	public ContainerContext(String name) {
		super(name);
		log.debug("Creating experiment-context '{}'", name);
	}

	public Map<String, String> getProperties() {
		return properties;
	}
}
