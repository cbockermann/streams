/**
 * 
 */
package stream.data.mapper;

import stream.data.AbstractDataProcessor;
import stream.data.Data;
import stream.util.Description;
import stream.util.Parameter;

/**
 * <p>
 * This simple processor adds a timestamp (current time in milliseconds) to all
 * processed data items.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 * 
 */
@Description(group = "Data Stream.Processing.Transformations.Data")
public class Timestamp extends AbstractDataProcessor {

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
	@Parameter
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @see stream.data.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data data) {

		if (data != null && key != null) {
			data.put(key, new Long(System.currentTimeMillis()));
		}

		return data;
	}
}
