/**
 * 
 */
package stream.io;

/**
 * @author chris,Hendrik
 * 
 */
public abstract class AbstractQueue implements Queue, QueueService {

	protected String id;

	protected int capacity = Integer.MAX_VALUE;

	/**
	 * @see stream.io.Source#getId()
	 */
	@Override
	public String getId() {
		return id;
	}

	/**
	 * @see stream.io.Source#setId(java.lang.String)
	 */
	@Override
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @see stream.io.Queue#setSize(java.lang.Integer)
	 */
	@Override
	public void setSize(Integer limit) {
		if (limit <= 0)
			throw new IllegalArgumentException();
		this.capacity = limit;
	}

	/**
	 * @see stream.io.Queue#getSize()
	 */
	@Override
	public Integer getSize() {
		return capacity;
	}
}