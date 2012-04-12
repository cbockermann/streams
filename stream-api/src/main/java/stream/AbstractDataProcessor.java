/**
 * 
 */
package stream;

import stream.runtime.Context;
import stream.runtime.annotations.Parameter;

/**
 * @author chris
 * 
 */
public abstract class AbstractDataProcessor implements Processor, ContextAware {

	protected transient Context context;
	protected String id;

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
	@Parameter(required = false)
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @see stream.Processor#reset()
	 */
	@Override
	public void init(Context ctx) throws Exception {
		context = ctx;
	}

	/**
	 * @see stream.Processor#destory()
	 */
	@Override
	public void finish() throws Exception {
	}
}
