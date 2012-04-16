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
	 * @see stream.Processor#resetState()
	 */
	@Override
	public void init(Context ctx) throws Exception {
		context = ctx;
	}

	/**
	 * @see stream.StatefulProcessor#resetState()
	 */
	@Override
	public void resetState() throws Exception {
	}

	/**
	 * @see stream.Processor#destory()
	 */
	@Override
	public void finish() throws Exception {
	}
}
