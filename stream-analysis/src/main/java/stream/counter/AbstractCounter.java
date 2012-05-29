/**
 * 
 */
package stream.counter;

import java.io.Serializable;

import stream.Processor;
import stream.data.Data;

/**
 * @author chris
 * 
 */
public abstract class AbstractCounter implements Processor,
		Counter<Serializable> {

	/** The unique class ID */
	private static final long serialVersionUID = -142354942331507845L;
	String key;

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

	/**
	 * @see stream.Processor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data input) {

		if (key == null || input == null)
			return input;

		Serializable value = input.get(key);
		if (value != null) {
			count(value);
		}

		return input;
	}
}
