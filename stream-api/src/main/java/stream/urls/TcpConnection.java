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

/**
 * @author chris
 * 
 */
public class TcpConnection {

	static Logger log = LoggerFactory.getLogger(TcpURLConnection.class);
	Socket connect;

	final String host;
	final Integer port;

	public TcpConnection(String host, Integer port) {
		this.host = host;
		this.port = port;
	}

	/**
	 * @see java.net.URLConnection#connect()
	 */
	public void connect() throws IOException {
		try {
			log.debug("Connecting via TCP to {}:{}", host, port);
			connect = new Socket(host, port);
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
}
