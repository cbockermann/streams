/**
 * 
 */
package streams.codec;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * This class implements a simple codec based on the default Java serialization.
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 *
 */
public class DefaultCodec<T extends Serializable> implements Codec<T> {

	/**
	 * @see stream.io.Codec#decode(byte[])
	 */
	@SuppressWarnings("unchecked")
	@Override
	public T decode(byte[] rawBytes) throws Exception {

		ByteArrayInputStream bais = new ByteArrayInputStream(rawBytes);
		ObjectInputStream ois = new ObjectInputStream(bais);
		T object = (T) ois.readObject();

		ois.close();
		return object;
	}

	/**
	 * @see stream.io.Codec#encode(java.lang.Object)
	 */
	@Override
	public byte[] encode(T object) throws Exception {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(object);

		oos.close();
		return baos.toByteArray();
	}
}