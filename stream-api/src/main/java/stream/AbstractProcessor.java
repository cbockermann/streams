/**
 * 
 */
package stream;


/**
 * @author chris
 * 
 */
public abstract class AbstractProcessor implements StatefulProcessor {

	protected transient Context context;

	/**
	 * @see stream.Processor#reset()
	 */
	@Override
	public void init(Context ctx) throws Exception {
		context = ctx;
	}

	/**
	 * @see stream.StatefulProcessor#reset()
	 */
	@Override
	public void reset() throws Exception {
	}

	/**
	 * @see stream.Processor#destory()
	 */
	@Override
	public void finish() throws Exception {
	}
}
