/**
 * 
 */
package stream.data;

import java.util.UUID;

import stream.Data;
import stream.Processor;
import stream.annotations.Parameter;

/**
 * This processor will add a random UUID to each data item. By default, the UUID
 * will be added as attribute <code>@uuid</code> to the item. The
 * <code>key</code> parameter can be used to specify a different attribute name.
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 * 
 */
public class AddUUID implements Processor {

	@Parameter(description = "The attribute name as which the UUID should be added to the item.", required = false)
	String key = "@uuid";

	/**
	 * @see stream.Processor#process(stream.Data)
	 */
	@Override
	public Data process(Data input) {

		if (key != null) {
			input.put(key, UUID.randomUUID().toString());
		}

		return input;
	}

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
	public void setKey(String key) {
		this.key = key;
	}
}
