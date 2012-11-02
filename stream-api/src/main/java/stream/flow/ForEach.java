/**
 * 
 */
package stream.flow;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.ProcessorList;

/**
 * @author chris
 * 
 */
public class ForEach extends ProcessorList {

	static Logger log = LoggerFactory.getLogger(ForEach.class);
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
	 * @see stream.ProcessorList#process(stream.Data)
	 */
	@Override
	public Data process(Data input) {

		if (key == null)
			return input;

		Serializable value = input.get(key);
		if (value == null)
			return input;

		ArrayList<Data> incol = new ArrayList<Data>();
		if (value instanceof Collection) {
			Iterator<?> it = ((Collection<?>) value).iterator();
			while (it.hasNext()) {
				Data item = (Data) it.next();
				incol.add(item);
			}
		} else {
			if (value.getClass().isArray()) {
				int len = Array.getLength(value);
				for (int i = 0; i < len; i++) {
					Data item = (Data) Array.get(value, i);
					if (item != null) {
						incol.add(item);
					}
				}
			} else {
				log.debug("Collection {} not supported!");
			}
		}

		ArrayList<Data> outcol = new ArrayList<Data>();
		for (Data item : incol) {

			item = super.process(item);
			if (item != null) {
				outcol.add(item);
			}
		}

		input.put(key, outcol.toArray(new Data[outcol.size()]));
		return input;
	}
}
