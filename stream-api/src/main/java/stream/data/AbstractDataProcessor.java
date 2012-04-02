/**
 * 
 */
package stream.data;

import stream.runtime.Context;

/**
 * @author chris
 * 
 */
public abstract class AbstractDataProcessor implements DataProcessor {

	protected String id;
	protected Context context;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @see stream.data.Processor#reset()
	 */
	@Override
	public void init(Context ctx) throws Exception {
		context = ctx;
	}

	/**
	 * @see stream.data.Processor#destory()
	 */
	@Override
	public void finish() throws Exception {
	}
}
