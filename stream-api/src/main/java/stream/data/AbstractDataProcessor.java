/**
 * 
 */
package stream.data;

/**
 * @author chris
 * 
 */
public abstract class AbstractDataProcessor implements DataProcessor {

	protected Context context;

	/**
	 * @see stream.data.Processor#init()
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
