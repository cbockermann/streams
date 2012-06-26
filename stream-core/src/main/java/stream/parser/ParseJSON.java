/**
 * 
 */
package stream.parser;

import java.io.Serializable;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Processor;
import stream.annotations.Description;
import stream.annotations.Parameter;
import stream.data.Data;

/**
 * @author chris
 * 
 */
@Description(group = "Streams.Processing.Transformations.Data")
public class ParseJSON implements Processor {

	static Logger log = LoggerFactory.getLogger(ParseJSON.class);
	String key = "@json";
	boolean remove = false;
	String prefix = null;

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key
	 *            the key to set
	 */
	@Parameter(required = false, description = "The attribute into which the JSON string of this item should be stored. Default is '@json'.")
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return the remove
	 */
	public boolean isRemove() {
		return remove;
	}

	/**
	 * @param remove
	 *            the remove to set
	 */
	@Parameter(required = false, description = "Set to 'true' if you want the attribute from which the JSON object has been parsed to be removed.")
	public void setRemove(boolean remove) {
		this.remove = remove;
	}

	/**
	 * @return the prefix
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * @param prefix
	 *            the prefix to set
	 */
	@Parameter(required = false, description = "An optional prefix that will be prepended to the attributes/keys parsed from the JSON string.")
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	/**
	 * @see stream.Processor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data input) {
		JSONParser parser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
		Serializable val = input.get(key);
		if (val == null)
			return input;

		String value = val.toString();

		try {
			JSONObject object = parser.parse(value, JSONObject.class);
			if (object == null)
				return input;

			// Add all keys from the JSON object to the data item
			//
			for (String key : object.keySet()) {
				Object data = object.get(key);
				if (data instanceof Serializable) {

					// if a prefix has been specified, this will be added to
					// the key before adding it to the data item
					//
					if (prefix != null) {
						input.put(prefix + key, (Serializable) data);
					} else {
						input.put(key, (Serializable) data);
					}
				} else {
					log.warn(
							"JSON object contains non-serializable object in property '{}': {}",
							key, data);
				}
			}

			if (remove && !object.containsKey(key))
				input.remove(key);

		} catch (Exception e) {
			log.error("Failed to parse JSON object from key {}: {}", key,
					e.getMessage());
			if (log.isDebugEnabled())
				e.printStackTrace();
		}

		return input;
	}
}
