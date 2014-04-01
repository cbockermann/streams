/**
 * 
 */
package stream.data;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.Processor;
import stream.io.Sink;

/**
 * @author chris
 * 
 */
public class Unfold implements Processor {

	static Logger log = LoggerFactory.getLogger(Unfold.class);
	String key = null;
	Sink[] output;

	/**
	 * @see stream.Processor#process(stream.Data)
	 */
	@Override
	public Data process(Data input) {

		if (key == null || output == null) {
			return input;
		}

		Serializable value = input.get(key);
		if (value == null)
			return input;

		if (value.getClass().isArray()) {

			for (int i = 0; i < Array.getLength(value); i++) {

				Object obj = Array.get(value, i);
				if (obj == null) {
					continue;
				}

				if (obj instanceof Serializable) {
					Data iteration = input.createCopy();
					iteration.put(key, (Serializable) obj);
					emit(iteration);
				} else {
					log.warn(
							"Cannot unfold item on key '{}' -- value '{}' for key is not serializable!",
							key, obj);
				}
			}

			return input;
		}

		if (value instanceof Collection) {

			Collection<?> col = (Collection<?>) value;
			Iterator<?> it = col.iterator();
			while (it.hasNext()) {
				Object obj = it.next();
				if (obj == null)
					continue;

				if (obj instanceof Serializable) {
					Data iteration = input.createCopy();
					iteration.put(key, (Serializable) obj);
					emit(iteration);
				} else {
					log.warn(
							"Cannot unfold item on key '{}' -- value '{}' for key is not serializable!",
							key, obj);
				}
			}

			return input;
		}

		emit(input.createCopy());
		return input;
	}

	public void emit(Data item) {
		if (output == null) {
			return;
		}

		for (Sink sink : output) {
			if (sink != null) {
				try {
					sink.write(item);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
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

	/**
	 * @return the output
	 */
	public Sink[] getOutput() {
		return output;
	}

	/**
	 * @param output
	 *            the output to set
	 */
	public void setOutput(Sink[] output) {
		this.output = output;
	}
}
