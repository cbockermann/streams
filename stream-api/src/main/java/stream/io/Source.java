/**
 * 
 */
package stream.io;

import stream.Data;

/**
 * @author Christian Bockermann,Hendrik Blom
 * 
 */
public interface Source {

	/**
	 * @return the id of this stream
	 */
	public abstract String getId();

	/**
	 * @param id
	 *            the id of the stream
	 */
	public abstract void setId(String id);

	/**
	 * This method will be called by the stream runtime at initialization time.
	 * Opening files, URLs or database connections is usually performed in this
	 * method.
	 * 
	 * @throws Exception
	 */
	public abstract void init() throws Exception;

	/**
	 * Returns the next datum from this stream.
	 * 
	 * @return
	 * @throws Exception
	 */
	public abstract Data read() throws Exception;

	/**
	 * This method is called by the stream runtime environment as the process
	 * container is shut down. This can be used to close file handles, streams
	 * or database connections.
	 * 
	 */
	public abstract void close() throws Exception;

}