/**
 * 
 */
package stream.data;

import stream.Data;
import stream.Processor;
import stream.annotations.Description;
import stream.annotations.Parameter;

/**
 * @author chris
 * 
 */
@Description(group = "Streams.Transformations.Annotations")
public class AddTimestamp implements Processor {

	String key = "@timestamp";

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
	@Parameter(description = "The key of the timestamp attribute to add")
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @see stream.Processor#process(stream.Data)
	 */
	@Override
	public Data process(Data input) {
		if (key != null) {
			input.put(key, System.currentTimeMillis());
		}
		return input;
	}
}