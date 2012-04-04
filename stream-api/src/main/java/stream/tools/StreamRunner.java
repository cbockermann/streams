/**
 * 
 */
package stream.tools;

import java.net.URL;

import stream.runtime.ProcessContainer;

/**
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 * @deprecated This has been moved to stream.runtime.ProcessContainer. The
 *             StreamRunner exists for compatibility only.
 * 
 */
public final class StreamRunner extends ProcessContainer {

	public StreamRunner(URL url) throws Exception {
		super(url);
	}
}
