/**
 * 
 */
package stream.storm;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.ProcessContext;

/**
 * @author chris
 * 
 */
public class BoltContext implements ProcessContext, Serializable {

	/** The unique class ID */
	private static final long serialVersionUID = 6162013508460469957L;
	static Logger log = LoggerFactory.getLogger(BoltContext.class);

	final Map<String, Serializable> values = new LinkedHashMap<String, Serializable>();
	transient Map<String, Object> volatileValues = new LinkedHashMap<String, Object>();

	/**
	 * @see stream.Context#resolve(java.lang.String)
	 */
	@Override
	public Object resolve(String variable) {
		if (!variable.startsWith("process.")) {
			log.warn("A BoltContext does currently not provide parent contexts.");
			return null;
		}

		return get(variable.substring("process.".length()));
	}

	// /**
	// * @see stream.Context#lookup(java.lang.String, java.lang.Class)
	// */
	// @Override
	// public <T extends Service> T lookup(String ref, Class<T> serviceClass)
	// throws Exception {
	// throw new UnsupportedOperationException(
	// "The service level is currently not implemented within storm!");
	// }

	/**
	 * @see stream.ProcessContext#get(java.lang.String)
	 */
	@Override
	public Object get(String key) {

		if (values.containsKey(key)) {
			log.debug("Found serialiable value for key '{}'", key);
			return values.get(key);
		}

		if (volatileValues.containsKey(key)) {
			log.debug("Found non-serializable value for key '{}'", key);
			return volatileValues.get(key);
		}

		log.debug("No value for key '{}' stored in this context.", key);
		return null;
	}

	/**
	 * @see stream.ProcessContext#set(java.lang.String, java.lang.Object)
	 */
	@Override
	public void set(String key, Object o) {

		if (o instanceof Serializable) {
			values.put(key, (Serializable) o);
			volatileValues.remove(key);
			return;
		}

		log.warn("Storing non-serializable object in context! The object might be lost during outages!");
		values.remove(key);
		volatileValues.put(key, o);
	}

	public Object readResolve() {
		if (this.volatileValues == null) {
			volatileValues = new LinkedHashMap<String, Object>();
		}
		return this;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
	}

	public boolean contains(String key) {

		if (values.containsKey(key))
			return true;

		if (volatileValues.containsKey(key))
			return true;
		return false;
	}
}
