/**
 * 
 */
package stream.io;

/**
 * @author chris
 * 
 */
public abstract class AbstractQueue implements Queue {

	protected String id;
	protected Integer limit = 1000;

	/**
	 * @see stream.io.Barrel#clear()
	 */
	@Override
	public int clear() {
		return 0;
	}

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
	 * @see stream.io.Source#init()
	 */
	@Override
	public void init() throws Exception {
	}

	/**
	 * @see stream.io.Source#close()
	 */
	@Override
	public void close() throws Exception {
	}

	/**
	 * @see stream.io.Queue#setLimit(java.lang.Integer)
	 */
	@Override
	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	/**
	 * @see stream.io.Queue#getLimit()
	 */
	@Override
	public Integer getLimit() {
		return limit;
	}
}