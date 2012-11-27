/**
 * 
 */
package stream.runtime;

import java.io.InputStream;

import stream.io.SourceURL;

/**
 * <p>
 * An interface for abstract container factory implementations.
 * </p>
 * 
 * @author Hendrik Blom, Christian Bockermann
 * 
 */
public interface ContainerFactory {

	/**
	 * Creates a container instance from a given source URL.
	 * 
	 * @param url
	 *            The URL that provides the XML definition.
	 * @return The container instance derived from the XML.
	 * @throws Exception
	 */
	public Container create(SourceURL url) throws Exception;

	/**
	 * Creates a container instance from a given InputStream.
	 * 
	 * @param in
	 *            The input stream that provides the XML definition.
	 * @return The container instance derived from the XML.
	 * @throws Exception
	 */
	public Container create(InputStream in) throws Exception;
}
