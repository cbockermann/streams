/**
 * 
 */
package stream.data;

/**
 * A processor is a simple class that follows a 3-step lifecycle. The lifecycle
 * starts with <code>init()</code>, after which several calls to the
 * <code>process</code> method may follow.
 * 
 * At the end of the lifecycle, the <code>finish()</code> method is called to
 * release and open connections or the like.
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public interface Processor<I, O> {

	/**
	 * This method is called once at initialization/setup time after the object
	 * has been instantiated and AFTER all parameters have been injected.
	 * 
	 * @throws Exception
	 */
	public void init() throws Exception;

	/**
	 * This is the main method for processing items. This method is called
	 * numerous times - once for each incoming data item.
	 * 
	 * @param input
	 * @return
	 */
	public O process(I input);

	/**
	 * This method is called at the time where the complete system is torn down.
	 * This may be used to close file-handles, connections or clean up internal
	 * memory.
	 * 
	 * @throws Exception
	 */
	public void finish() throws Exception;
}
