/**
 * 
 */
package stream.io;

import java.util.LinkedHashSet;
import java.util.Set;

import stream.ConditionedProcessor;
import stream.Data;
import stream.Keys;
import stream.annotations.Parameter;

/**
 * @author chris
 * 
 */
public abstract class AbstractWriter extends ConditionedProcessor {

	String url;
	protected Keys keys;

	/**
	 * @see stream.ConditionedProcessor#processMatchingData(stream.Data)
	 */
	@Override
	public Data processMatchingData(Data data) {
		try {
			write(data);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		return data;
	}

	/**
	 * @return the keys
	 */
	public Keys getKeys() {
		return keys;
	}

	/**
	 * @param keys
	 *            the keys to set
	 */
	@Parameter(description = "The keys, which shall be written out (supports wildcards *, ? and negation with !", defaultValue = "*")
	public void setKeys(Keys keys) {
		this.keys = keys;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url
	 *            the url to set
	 */
	@Parameter(description = "The URL to write to, currently, only file URLs are supported.", required = true)
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * This class returns the keys that need to be written based on the
	 * selection pattern.
	 * 
	 * @param item
	 *            The item to extract keys from.
	 * @return The keys matching the selection pattern.
	 */
	protected Set<String> selectedKeys(Data item) {
		if (keys == null) {
			return new LinkedHashSet<String>(item.keySet());
		} else {
			return keys.select(item.keySet());
		}
	}

	/**
	 * This method is actually responsible for writing the data item to the
	 * output stream in the format that the implementing class supports.
	 * 
	 * @param item
	 * @throws Exception
	 */
	public abstract void write(Data item) throws Exception;
}
