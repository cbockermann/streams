package stream.util;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.Processor;
import stream.annotations.Parameter;

/**
 * @author hendrik
 * 
 */
public class CreateUUID implements Processor {

	static Logger log = LoggerFactory.getLogger(CreateUUID.class);

	protected String key;

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
	@Parameter(description = "The key/name of the variable to set.")
	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public Data process(Data data) {
		UUID uuid = UUID.randomUUID();
		data.put(key, uuid);
		return data;
	}

}