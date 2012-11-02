/**
 * 
 */
package stream.flow;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.Processor;
import stream.data.DataFactory;

/**
 * @author chris
 * 
 */
public class Collect implements Processor {

	static Logger log = LoggerFactory.getLogger(Collect.class);

	String key = "@items";

	Integer count = 1;

	List<Data> items = new ArrayList<Data>();

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
	 * @return the count
	 */
	public Integer getCount() {
		return count;
	}

	/**
	 * @param count
	 *            the count to set
	 */
	public void setCount(Integer count) {
		this.count = count;
	}

	/**
	 * @see stream.Processor#process(stream.Data)
	 */
	@Override
	public Data process(Data input) {

		if (items.size() < count) {
			items.add(DataFactory.create(input));
			return null;
		} else {
			Data[] vals = new Data[items.size()];
			for (int i = 0; i < vals.length; i++) {
				vals[i] = items.get(i);
			}
			input.put(key, vals);
		}

		return input;
	}
}
