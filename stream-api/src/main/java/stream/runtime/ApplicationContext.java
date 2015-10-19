/**
 * 
 */
package stream.runtime;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import stream.Context;
import stream.util.Variables;

/**
 * @author chris
 *
 */
public class ApplicationContext implements Context, Serializable {

	private static final long serialVersionUID = -5614833980900180506L;

	final String id;
	final Map<String, Object> content = new LinkedHashMap<String, Object>();

	public ApplicationContext(String id, Variables variables) {
		this.id = id;
		this.content.putAll(variables);
	}

	/**
	 * @see stream.Context#resolve(java.lang.String)
	 */
	@Override
	public Object resolve(String key) {
		if (key.equals("id")) {
			return id;
		}

		return content.get(key);
	}

	/**
	 * @see stream.Context#contains(java.lang.String)
	 */
	@Override
	public boolean contains(String key) {
		if (key.equals("id")) {
			return true;
		}

		return content.containsKey(key);
	}

	/**
	 * @see stream.Context#getId()
	 */
	@Override
	public String getId() {
		return id;
	}
}
