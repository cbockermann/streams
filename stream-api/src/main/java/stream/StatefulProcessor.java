/**
 * 
 */
package stream;


/**
 * This interface is implemented by processors requiring a context. The context
 * is provided by the runtime-environment.
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public interface StatefulProcessor extends Processor {

	/**
	 * This method is called at initialization time.
	 * 
	 * @param context
	 * @throws Exception
	 */
	public void init(Context context) throws Exception;

	public void reset() throws Exception;

	public void finish() throws Exception;
}
