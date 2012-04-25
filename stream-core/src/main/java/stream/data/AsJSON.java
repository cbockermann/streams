/**
 * 
 */
package stream.data;

import net.minidev.json.JSONObject;
import stream.Processor;
import stream.annotations.Description;
import stream.annotations.Parameter;

/**
 * @author chris
 * 
 */
@Description(group = "Data Stream.Processing.Transformations.Data")
public class AsJSON implements Processor {

	String key = "@json";

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
	 * @see stream.Processor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data input) {
		String json = JSONObject.toJSONString(input);
		input.put(key, json);
		return input;
	}
}
