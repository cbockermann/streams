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

	/**
	 * This method is called by the stream runtime environment as the process
	 * container is shut down. This can be used to close file handles, streams
	 * or database connections.
	 * 
	 */
	public abstract void close() throws Exception;
}
