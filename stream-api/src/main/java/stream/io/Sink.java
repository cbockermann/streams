/**
 * 
 */
package stream.io;

import stream.Data;

/**
 * <p>
 * This interface specifies a sink for data, i.e. any element that can receive
 * data items (e.g. Queues).
 * </p>
 * 
 * @author Christian Bockermann, Hendrik Blom
 * 
 */
public interface Sink {

	public String getId();

	/**
	 * Writes data into the instance represented by this sink.
	 * 
	 * @param item
	 * @throws Exception
	 */
	public void write(Data item) throws Exception;
}
