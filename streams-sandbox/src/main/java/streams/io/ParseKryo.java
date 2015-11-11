/**
 * 
 */
package streams.io;

import java.time.LocalDateTime;

import stream.AbstractProcessor;
import stream.Data;

/**
 * This processor uses the Kryo protocol to parse a data item object from a
 * kryo-based representation, stored in a byte array. All attributes of the
 * de-serialized item will be added to the data item which the byte array
 * originates from.
 * 
 * @author Christian Bockermann <christian.bockermann@udo.edu>
 *
 */
public class ParseKryo extends AbstractProcessor {

	String key = "data";
	final KryoCodec codec = new KryoCodec();

	public ParseKryo() {
		codec.serializer().register(LocalDateTime.class, new LocalDateTimeSerializer());
	}

	/**
	 * @see stream.Processor#process(stream.Data)
	 */
	@Override
	public Data process(Data input) {

		try {
			byte[] data = (byte[]) input.get(key);

			Data decoded = codec.decode(data);
			input.putAll(decoded);

		} catch (Exception e) {
			e.printStackTrace();
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
