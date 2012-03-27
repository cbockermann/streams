/**
 * 
 */
package stream.data;

/**
 * <p>
 * This interface provides a simple processing unit for streaming data.
 * Processing can be either read-only or with altering of the data.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 * 
 */
public interface DataProcessor extends Processor {

	/**
	 * This method is called once at initialization/setup time after the object
	 * has been instantiated and AFTER all parameters have been injected.
	 * 
	 * @throws Exception
	 */
	public void init(Context context) throws Exception;

	/**
	 * This method is called at the time where the complete system is torn down.
	 * This may be used to close file-handles, connections or clean up internal
	 * memory.
	 * 
	 * @throws Exception
	 */
	public void finish() throws Exception;

	/**
	 * Process the given unit of data.
	 * 
	 * @param data
	 *            The data item to be processed.
	 * @return The data after being processed.
	 */
	public Data process(Data data);
}
