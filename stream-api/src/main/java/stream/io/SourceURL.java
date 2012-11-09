/**
 * 
 */
package stream.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.urls.TcpConnection;
import stream.util.parser.Parser;
import stream.util.parser.ParserGenerator;

/**
 * @author chris
 * 
 */
public final class SourceURL implements Serializable {

	/** The unique class ID */
	private static final long serialVersionUID = -7992522266824113404L;

	static Logger log = LoggerFactory.getLogger(SourceURL.class);

	final static String FILE_GRAMMAR = "%(protocol):%(path)";
	final static String GRAMMAR = "%(protocol)://%(host):%(port)/%(path)";

	final URL url;
	final String urlString;

	final String protocol;
	final String host;
	final int port;
	final String path;

	public SourceURL(URL url) {
		this.url = url;
		this.urlString = url.toString();

		protocol = url.getProtocol();
		host = url.getHost();
		port = url.getPort();
		path = url.getPath();
	}

	public SourceURL(String urlString) throws Exception {
		this.url = null;
		this.urlString = urlString;

		if (urlString.toLowerCase().startsWith("file:")
				|| urlString.toLowerCase().startsWith("classpath")) {
			ParserGenerator gen = new ParserGenerator(FILE_GRAMMAR);
			Parser<Map<String, String>> parser = gen.newParser();
			Map<String, String> vals = parser.parse(urlString);
			protocol = vals.get("protocol");
			host = null;
			port = -1;
			path = vals.get("path");
		} else {
			ParserGenerator gen = new ParserGenerator(GRAMMAR);
			Parser<Map<String, String>> parser = gen.newParser();
			Map<String, String> vals = parser.parse(urlString);
			protocol = vals.get("protocol");
			host = vals.get("host");
			if (vals.get("port") == null || "".equals(vals.get("port").trim())) {
				port = -1;
			} else
				port = new Integer(vals.get("port"));
			path = vals.get("path");
		}
	}

	public InputStream openStream() throws IOException {

		if (this.url != null) {
			return url.openStream();
		}

		if ("classpath".equalsIgnoreCase(protocol)) {
			log.debug("Returning InputStream for classpath resource '{}'",
					getPath());
			return SourceURL.class.getResourceAsStream(getPath());
		}

		if ("tcp".equalsIgnoreCase(protocol)) {
			TcpConnection con = new TcpConnection(host, port);
			return con.getInputStream();
		}

		try {
			URL url = new URL(this.urlString);

			if (urlString.toLowerCase().endsWith(".gz")) {
				return new GZIPInputStream(url.openStream());
			}

			return url.openStream();
		} catch (Exception e) {
			log.error(
					"Failed to open '{}' with default Java URL mechanism: {}",
					urlString, e.getMessage());
			throw new IOException("No handler found for protocol '" + protocol
					+ "'!");
		}

	}

	public String getFile() {
		return path;
	}

	/**
	 * @return the protocol
	 */
	public String getProtocol() {
		return protocol;
	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}
}