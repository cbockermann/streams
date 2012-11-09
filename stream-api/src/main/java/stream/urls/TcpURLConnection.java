/**
 * 
 */
package stream.urls;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chris
 * 
 */
public class TcpURLConnection extends URLConnection {

	static Logger log = LoggerFactory.getLogger(TcpURLConnection.class);
	Socket connect;

	/**
	 * @param arg0
	 */
	protected TcpURLConnection(URL arg0) {
		super(arg0);
		log.debug("Creating TcpURLConnection for URL {}", arg0);
	}

	public TcpURLConnection() {
		super(null);
	}

	/**
	 * @see java.net.URLConnection#connect()
	 */
	@Override
	public void connect() throws IOException {
		String host = this.url.getHost();
		Integer port = this.url.getPort();
		if (port == null)
			port = 10001;

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
	@Override
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
	@Override
	public OutputStream getOutputStream() throws IOException {
		if (connect == null || !connect.isConnected()) {
			log.error("Tried to access output-stream but no connection has been established, yet!");
			throw new IOException("TcpURLConnectio not yet established!");
		}

		log.debug("Returning output-stream for TCP connection {}", connect);
		return connect.getOutputStream();
	}
}
