/**
 * 
 */
package stream.io;

/**
 * This interface defines an abstract codec, used for converting objects of some
 * type into a byte-code representation and vice-versa.
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 *
 */
public interface Codec<T> {

	/**
	 * Converts the given raw byte data representation into an object.
	 * 
	 * @param rawBytes
	 * @return
	 * @throws Exception
	 */
	public T decode(byte[] rawBytes) throws Exception;

	/**
	 * Encodes the given object into a raw byte data representation.
	 * 
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public byte[] encode(T object) throws Exception;
}
