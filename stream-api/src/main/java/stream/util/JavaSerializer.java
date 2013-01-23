/**
 * 
 */
package stream.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.io.Serializer;

/**
 * <p>
 * An implementation of the Serializer interface that uses the plain built-in
 * Java serialization.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 * 
 */
public class JavaSerializer implements Serializer {

	final static Logger log = LoggerFactory.getLogger(JavaSerializer.class);

	/**
	 * @see stream.io.Serializer#clone(java.io.Serializable)
	 */
	@Override
	public Serializable clone(Serializable object) throws Exception {
		if (object == null)
			return null;

		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			write(object, baos);
			baos.flush();
			baos.close();
			ByteArrayInputStream bais = new ByteArrayInputStream(
					baos.toByteArray());
			Serializable value = read(bais);
			bais.close();

			return value;
		} catch (Exception e) {
			log.error("Cloning object {} failed: {}", object, e.getMessage());
			throw new Exception(e.getMessage());
		}
	}

	/**
	 * @see stream.io.Serializer#read(java.io.InputStream)
	 */
	@Override
	public Serializable read(InputStream in) throws IOException {
		try {
			ObjectInputStream ois = new ObjectInputStream(in);
			Serializable value = (Serializable) ois.readObject();
			return value;
		} catch (Exception e) {
			log.error(
					"De-serialization of object from input-stream {} failed: {}",
					in, e.getMessage());
			throw new IOException(e.getMessage());
		}
	}

	/**
	 * @see stream.io.Serializer#write(java.io.Serializable,
	 *      java.io.OutputStream)
	 */
	@Override
	public void write(Serializable object, OutputStream out) throws IOException {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(out);
			oos.writeObject(object);
			oos.flush();
		} catch (Exception e) {
			log.error("Serialization of object {} failed: {}", object,
					e.getMessage());
			throw new IOException(e.getMessage());
		}
	}
}