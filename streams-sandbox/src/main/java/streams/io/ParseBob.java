/**
 * 
 */
package streams.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.Processor;
import stream.io.Codec;
import stream.io.JavaCodec;

/**
 * This processor extracts a serialized data item from a raw-byte attribute of
 * the processed item. The attributes of the resulting item are merged into the
 * processed item.
 * 
 * This allows for raw, low-level byte events to be passed around and distribute
 * the deserialization to separate threads.
 * 
 * @author Christian Bockermann
 *
 */
public class ParseBob implements Processor {

	static Logger log = LoggerFactory.getLogger(ParseBob.class);

	String key = "data";
	Codec<Data> serializer = new JavaCodec<Data>();

	/**
	 * @see stream.Processor#process(stream.Data)
	 */
	public Data process(Data input) {

		byte[] bytes = (byte[]) input.get(key);
		if (bytes != null) {
			try {
				Data item = serializer.decode(bytes);
				input.putAll(item);
			} catch (Exception e) {
				log.error("Failed to de-serialize item: {}", e.getMessage());
				if (log.isDebugEnabled()) {
					e.printStackTrace();
				}
			}
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

	/**
	 * @return the serializer
	 */
	public String getSerializer() {
		return serializer.getClass().getName();
	}

	/**
	 * @param serializer
	 *            the serializer to set
	 */
	@SuppressWarnings("unchecked")
	public void setSerializer(String serializer) {
		try {
			Class<?> clazz = Class.forName(serializer);
			this.serializer = (Codec<Data>) clazz.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}