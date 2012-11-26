/**
 * 
 */
package stream.io;

/**
 * <p>
 * A barrel is any class that can receive data items, store them for a limited
 * amount of time and provide these data items as a source. Examples for barrels
 * are queues.
 * </p>
 * 
 * @author Hendrik Blom, Christian Bockermann
 * 
 */
public interface Barrel extends Sink, Source {

	/**
	 * This method removes all elements currently stored in this barrel and
	 * returns the number of elements discarded.
	 * 
	 * @return The number of elements removed from this barrel.
	 */
	public int clear();
}
