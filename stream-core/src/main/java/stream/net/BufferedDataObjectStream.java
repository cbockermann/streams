/**
 * 
 */
package stream.net;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ConnectException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.annotations.Parameter;
import stream.io.DataObjectStream;
import stream.io.SourceURL;
import stream.util.parser.TimeParser;

/**
 * Adds a receive buffer to {@link DataObjectStream} for performance reasons and
 * adds a reconnect functionality in case of an error.
 * 
 * @author Hendrik Blom, Tobias Beckers
 * 
 */
public class BufferedDataObjectStream extends DataObjectStream {

	private final Logger log = LoggerFactory
			.getLogger(BufferedDataObjectStream.class);

	// STREAMS-PARAMETERS

	/**
	 * The buffer size (in bytes) used by this stream. If set to &leq; 0: The
	 * {@link BufferedInputStream}'s default buffer size is taken (usually 8192)
	 */
	protected int bufferSize;
	/**
	 * the maximum amount of connection retries before the general connection
	 * attempt finally fails
	 */
	protected int connectionRetries;
	/**
	 * the time between two connection attempts if the first connection attempt
	 * was unsuccessful
	 */
	protected long reconnectInterval;

	// OTHER CLASS FIELDS

	/** caches the return value of {@link #getConnectionString()} */
	protected String urlCache;

	protected boolean connected;

	// CONSTRUCTORS

	/** invokes {@link DataObjectStream#DataObjectStream(InputStream)} */
	public BufferedDataObjectStream(InputStream in) {
		super(in);

	}

	/** invokes {@link DataObjectStream#DataObjectStream(SourceURL)} */
	public BufferedDataObjectStream(SourceURL url) {
		super(url);
	}

	// GETTER AND SETTER FOR STREAMS-PARAMETERS

	public int getBufferSize() {
		return bufferSize;
	}

	@Parameter(required = false, defaultValue = "0", description = "The buffer size (in bytes) used by this stream. If set to <= 0: The BufferedInputStream's default buffer size is taken (usually 8192)")
	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	public int getConnectionRetries() {
		return connectionRetries;
	}

	@Parameter(required = false, defaultValue = "Integer.MAX_VALUE", description = "the maximum amount of connection retries before the general connection attempt finally fails")
	public void setConnectionRetries(int connectionRetries) {
		this.connectionRetries = connectionRetries;
	}

	public long getReconnectInterval() {
		return reconnectInterval;
	}

	@Parameter(required = false, defaultValue = "5s", description = "the time between two connection attempts if the first connection attempt was unsuccessful")
	public void setReconnectInterval(String reconnectIntervalString)
			throws Exception {
		this.reconnectInterval = TimeParser.parseTime(reconnectIntervalString);
	}

	/** {@inheritDoc} */
	@Override
	public void init() throws Exception {
		input = null;
		in = null;
		bufferSize = 0;
		connectionRetries = Integer.MAX_VALUE;
		reconnectInterval = 5000;
		urlCache = null;
		connected = false;
	}

	/**
	 * Connects to the specified URL. Retries to connect up to
	 * {@link #connectionRetries} times if not successful.
	 * 
	 * @throws ConnectException
	 *             if it is not possible to connect to the target
	 */
	protected void connect() throws ConnectException {

		// close old connection if there was one
		close();
		boolean connected = false;
		int connectionRetryCounter = 0;

		while (!connected && connectionRetryCounter <= connectionRetries) {
			try {
				log.info("Trying to open connection to {} ...",
						getConnectionString());
				if (connectionRetryCounter > 0)
					log.info("Connection retry counter: {}",
							connectionRetryCounter);

				// get socket input stream
				InputStream innerIn = getInputStream();
				// add input buffer to input stream
				innerIn = (bufferSize > 0) ? new BufferedInputStream(innerIn,
						bufferSize) : new BufferedInputStream(innerIn);
				// add deserialization stream
				input = new ObjectInputStream(innerIn);

				connected = true;
			} catch (Exception e) {
				log.warn("Unable to connect to {}: {}", getConnectionString(),
						e.toString());
				close();
				try {
					Thread.sleep(reconnectInterval);
				} catch (InterruptedException ignore) {
				}
				connectionRetryCounter++;
			}
		}

		if (connected)
			log.info("Successfully connected to {}", getConnectionString());
		else {
			log.error(
					"Giving up connection attempt after {} retries. Connection to {} unavailable.",
					connectionRetryCounter - 1, getConnectionString());
			close();
			throw new ConnectException();
		}
	}

	/**
	 * <p>
	 * {@inheritDoc}
	 * </p>
	 * <p>
	 * See {@link BufferedDataObjectStream} for details.
	 * </p>
	 */
	@Override
	public Data readNext() throws ConnectException {

		if (!connected) {
			connect();
			connected = true;
		}
		while (true) {
			try {
				return (Data) input.readObject();
			} catch (Exception e) {
				// EOFException and SocketException indicate a connection /
				// socket error / network lost
				// reconnect / reinitialize if any exception is thrown
				log.error(
						"Exception while reading data from socket stream {}: {}",
						getConnectionString(), e.toString());
				log.info("Trying to restart connection to {} ...",
						getConnectionString());
				connect();
			}
		}
	}

	/**
	 * <p>
	 * Returns a string representing the connection url (works currently only
	 * with tcp connections).
	 * </p>
	 * 
	 * TODO Better: Implement and call SourceURL.toString() method
	 * 
	 * @return a string representing the connection url
	 */
	protected String getConnectionString() {
		if (urlCache == null) {
			StringBuilder sb = new StringBuilder(url.getProtocol());
			sb.append("://");
			sb.append(url.getHost());
			sb.append(":");
			sb.append(url.getPort());
			urlCache = sb.toString();
		}
		return urlCache;
	}

	/**
	 * <p>
	 * Closes / Resets this connection such that all resources are freed and the
	 * next invocation of {@link #getInputStream()} will return a fresh new
	 * connection to the target.
	 * </p>
	 * 
	 * <p>
	 * Does nothing if the connection is not open
	 * </p>
	 */
	@Override
	public void close() {
		// stop if nothing exists to be closed
		if (input == null && in == null)
			return;

		log.info("Closing connection to {} ...", getConnectionString());
		try {
			// free input stream resources
			if (input != null)
				input.close();
			if (in != null)
				in.close();
		} catch (Exception e) {
			log.warn("Exception while closing connection to {}: {}",
					getConnectionString(), e.toString());
		}
		// reset input streams to null
		input = null;
		in = null;
		log.info("Connection to {} closed.", getConnectionString());
	}
}