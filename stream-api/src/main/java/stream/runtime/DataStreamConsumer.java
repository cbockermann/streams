/**
 * 
 */
package stream.runtime;

import stream.io.DataStream;

/**
 * This interface marks a consumer of a data-stream. The consumer provides the
 * data-stream name as String (<code>getInput()</code>) and a setter to have the
 * data-stream injected.
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public interface DataStreamConsumer {

	/**
	 * Returns the name of the data-stream the implementing instance want to
	 * consume.
	 * 
	 * @return
	 */
	public String getInput();

	/**
	 * Sets the data-stream of this consumer.
	 * 
	 * @param stream
	 */
	public void setDataStream(DataStream stream);
}
