/**
 * 
 */
package stream.urls;

import java.io.IOException;
import java.io.InputStream;

import stream.io.SourceURL;

/**
 * <p>
 * This class provides an abstract connection to some data source. The source is
 * specified by a {@link stream.io.SourceURL} object. Implementations of this
 * class are providing access to those data sources by means of
 * protocol-specific handling.
 * </p>
 * 
 * @author Christian Bockermann
 * 
 */
public abstract class Connection {

	/** The URL to which this instance should connect to */
	final SourceURL url;

	public Connection(SourceURL url) {
		this.url = url;
	}

	/**
	 * A list of supported protocol for the implementing class. Usually a class
	 * implements supports for a single protocol.
	 * 
	 * @return
	 */
	public abstract String[] getSupportedProtocols();

	/**
	 * This opens the connection and returns the input stream for the
	 * connection.
	 * 
	 * @return
	 * @throws IOException
	 */
	public abstract InputStream connect() throws IOException;

	/**
	 * This disconnects from the source (if previously connected) and releases
	 * all resources aquired.
	 * 
	 * @throws IOException
	 */
	public abstract void disconnect() throws IOException;
}
