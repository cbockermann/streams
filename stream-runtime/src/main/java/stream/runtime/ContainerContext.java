/**
 * 
 */
package stream.runtime;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Context;

/**
 * @author chris
 * 
 */
public class ContainerContext extends DefaultLookupService implements Context {

	final static String CONTEXT_NAME = "container";
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

	public void setProperty(String key, String value) {
		if (value == null)
			properties.remove(key);
		else
			properties.put(key, value);
	}

	/**
	 * @see stream.Context#resolve(java.lang.String)
	 */
	@Override
	public Object resolve(String variable) {

		if (variable == null)
			return null;

		String var = variable.trim();
		if (var.startsWith("container.")) {
			String key = var.substring(CONTEXT_NAME.length() + 1);
			if (properties.containsKey(key))
				return properties.get(key);
		}

		return null;
	}
}
