/**
 * 
 */
package stream.io;

/**
 * @author chris
 * 
 */
public class BlockingQueue extends DataStreamQueue {

	Integer size = 10;

	/**
	 * @return the size
	 */
	public Integer getSize() {
		return size;
	}

	/**
	 * @param size
	 *            the size to set
	 */
	public void setSize(Integer size) {
		this.size = size;
	}
}
