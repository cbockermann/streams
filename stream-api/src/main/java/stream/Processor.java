/**
 * 
 */
package stream;

import stream.data.Data;

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
public interface Processor {

	/**
	 * This is the main method for processing items. This method is called
	 * numerous times - once for each incoming data item.
	 * 
	 * @param input
	 * @return
	 */
	public Data process(Data input);

}
