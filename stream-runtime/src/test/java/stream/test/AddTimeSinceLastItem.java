/**
 * 
 */
package stream.test;

import stream.AbstractProcessor;
import stream.Data;

/**
 * 
 * @author Christian Bockermann
 * 
 */
public class AddTimeSinceLastItem extends AbstractProcessor {

	int id = 1;
	long last = -1L;
	String key = "@delay";

	/**
	 * @see stream.Processor#process(stream.Data)
	 */
	@Override
	public Data process(Data input) {
		input.clear();
		input.put("@id", id++);
		long now = System.currentTimeMillis();
		input.put("@timestamp", now);
		if (last < 0)
			last = now;

		if (key != null)
			input.put(key, now - last);
		last = now;
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
