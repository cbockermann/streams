/**
 * 
 */
package stream.data;

import java.io.Serializable;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.Keys;
import stream.Processor;
import stream.annotations.Parameter;
import stream.util.parser.TypeParser;

/**
 * <p>
 * This processor removes key/value pairs from the processed items that have
 * values matching a give set of types. This allows for example to remove all
 * entries from items that are of type `java.lang.Double`.
 * </p>
 * <p>
 * If an additional pattern of `keys` is provided, then only the matching keys
 * will be checked for removal and any non-matching keys are guaranteed to
 * remain in the item untouched.
 * </p>
 * 
 * @author Christian Bockermann <chris@jwall.org>
 * 
 */
public class RemoveTypes implements Processor {

	static Logger log = LoggerFactory.getLogger(RemoveTypes.class);

	Class<?>[] classes = new Class<?>[0];

	String[] keys;
	String[] types = new String[0];

	/**
	 * @see stream.Processor#process(stream.Data)
	 */
	@Override
	public Data process(Data input) {

		Iterator<String> it = input.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();

			if (keys != null && !Keys.isSelected(key, keys)) {
				log.debug("Skipping non-selected key {}", key);
				continue;
			}

			Serializable value = input.get(key);

			if (toRemove(value.getClass())) {
				log.debug(
						"Removing key '{}' as type '{}' is matching the set of types to remove!",
						key, value.getClass());
				it.remove();
			} else {
				log.debug("Type {} not set to be removed.", value.getClass());
			}
		}

		return input;
	}

	protected boolean toRemove(Class<?> type) {

		for (Class<?> t : classes) {
			if (t.equals(type)) {
				return true;
			}

			if (t.isAssignableFrom(type)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return the keys
	 */
	public String[] getKeys() {
		return keys;
	}

	/**
	 * @param keys
	 *            the keys to set
	 */
	public void setKeys(String[] keys) {
		this.keys = keys;
	}

	/**
	 * @return the types
	 */
	public String[] getTypes() {
		return types;
	}

	/**
	 * @param types
	 *            the types to set
	 */
	@Parameter(description = "The list of types that are to be removed. Append `[]` to a type to refer to an array of that type.", required = true)
	public void setTypes(String[] types) {
		this.types = types;
		this.classes = TypeParser.parse(types);
	}
}