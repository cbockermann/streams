/**
 * 
 */
package stream.urls;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.io.SourceURL;

/**
 * <p>
 * A simple implementation of the abstract {@link Connection} class that
 * supports connecting to TCP network sockets.
 * </p>
 * 
 * @author Christian Bockermann
 * 
 */
public class TcpConnection extends Connection {

	static Logger log = LoggerFactory.getLogger(TcpConnection.class);

	public final static String PARAM_RECONNECT = "reconnect";
	public final static String PARAM_RECONNECT_INTERVAL = "reconnectInterval";

	protected Socket connect;

	protected final String host;
	protected final Integer port;

	public TcpConnection(SourceURL url) {
		super(url);

		this.host = url.getHost();
		this.port = url.getPort();
	}

	/**
	 * @see java.net.URLConnection#connect()
	 */
	public InputStream connect() throws IOException {
		try {
			log.debug("Connecting via TCP to {}:{}", host, port);
			connect = new Socket(host, port);
			return connect.getInputStream();
		} catch (IOException e) {
			log.error("Connection failed: {}", e.getMessage());
			throw e;
		}
	}

	/**
	 * @see java.net.URLConnection#getInputStream()
	 */
	public InputStream getInputStream() throws IOException {

		if (connect == null) {
			connect();
		}

		if (connect == null) {
			log.error("Tried to access input-stream but no connection has been established, yet!");
			throw new IOException("TcpURLConnection not yet established!");
		}

		log.debug("Returning input-stream for TCP connection {}", connect);
		return connect.getInputStream();
	}

	/**
	 * @see java.net.URLConnection#getOutputStream()
	 */
	public OutputStream getOutputStream() throws IOException {
		if (connect == null || !connect.isConnected()) {
			log.error("Tried to access output-stream but no connection has been established, yet!");
			throw new IOException("TcpURLConnectio not yet established!");
		}

		log.debug("Returning output-stream for TCP connection {}", connect);
		return connect.getOutputStream();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TcpConnection(" + super.toString() + ")[" + host + ":" + port
				+ "]";
	}

	/**
	 * @see stream.urls.Connection#getSupportedProtocols()
	 */
	@Override
	public String[] getSupportedProtocols() {
		return new String[] { "tcp" };
	}

	/**
	 * @see stream.urls.Connection#disconnect()
	 */
	@Override
	public void disconnect() throws IOException {
		if (connect != null) {
			connect.close();
		}
	}
}