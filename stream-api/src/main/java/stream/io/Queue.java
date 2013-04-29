/**
 * 
 */
package stream.io;

/**
 * <p>
 * This interface is the top-level definition of queues provided within the
 * *streams* framework. Queues provide a limited space for temporarily storing
 * data items in main memory.
 * </p>
 * 
 * @author Hendrik Blom, Christian Bockermann
 * 
 */
public interface Queue extends Barrel {

	/**
	 * The maximum size the implementation of this queue is able to hold.
	 * 
	 * @param limit
	 */
	public void setSize(Integer limit);

	/**
	 * Returns the maximum size of this queue implementation.
	 * 
	 * @return
	 */
	public Integer getSize();
}
