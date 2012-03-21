/**
 * 
 */
package stream.data;

/**
 * @author chris
 * 
 */
public abstract class AbstractDataProcessor implements DataProcessor {

	/**
	 * @see stream.data.Processor#init()
	 */
	@Override
	public void init() throws Exception {
	}

	/**
	 * @see stream.data.Processor#destory()
	 */
	@Override
	public void finish() throws Exception {
	}
}
