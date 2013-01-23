/**
 * 
 */
package stream.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

/**
 * <p>
 * This interface defines an abstract serializer that provides clone and
 * serializing/deserializing of objects.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 * 
 */
public interface Serializer {

	/**
	 * Creates a clone of the given object by serializing and de-serializing it.
	 * 
	 * @param object
	 * @return
	 */
	public Serializable clone(Serializable object) throws Exception;

	/**
	 * Reads (de-serializes) an object from the given input stream.
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public Serializable read(InputStream in) throws IOException;

	/**
	 * Writes (serializes) an object into the given output stream.
	 * 
	 * @param object
	 * @param out
	 * @throws IOException
	 */
	public void write(Serializable object, OutputStream out) throws IOException;
}
