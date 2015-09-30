/**
 * 
 */
package stream.data;

import java.io.Serializable;

import stream.Data;
import stream.Processor;
import stream.annotations.Parameter;

/**
 * This processor computes the MD5 sum of a byte array provided in some input
 * field of the data items.
 * 
 * @author Christian Bockermann
 *
 */
public class MD5 implements Processor {

	String input = "data";
	String key = "@md5";

	/**
	 * @see stream.Processor#process(stream.Data)
	 */
	@Override
	public Data process(Data input) {

		Serializable value = input.get(this.input);
		if (value != null) {
			try {
				byte[] bytes = (byte[]) value;
				String sum = stream.util.MD5.md5(bytes);
				input.put(key, sum);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return input;
	}

	/**
	 * @return the input
	 */
	public String getInput() {
		return input;
	}

	/**
	 * @param input
	 *            the input to set
	 */
	@Parameter(description = "The attribute holding the byte array that shall be used for checksum computation.")
	public void setInput(String input) {
		this.input = input;
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
	@Parameter(description = "The name of the attribute where to store the MD5 sum, default is '@md5'.")
	public void setKey(String key) {
		this.key = key;
	}
}
