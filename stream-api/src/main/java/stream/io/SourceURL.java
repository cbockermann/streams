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
 * <p>
 * This URL encapsulates the definition of URLs for resources. It introduces a
 * thin layer of abstraction for providing support for more than the existing
 * protocol types in Java. The reason for introducing this SourceURL class is
 * that we do not want to register a custom protocol handler, which might
 * destroy some existing applications that also require a custom protocol
 * handler. Java only allows to register a single custom protocol handler.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
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
	final String username;
	final String password;

	public SourceURL(URL url) {
		this.url = url;
		this.urlString = url.toString();

		protocol = url.getProtocol();
		host = url.getHost();
		port = url.getPort();
		path = url.getPath();
		username = null;
		password = null;
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
			username = null;
			password = null;
			port = -1;
			path = vals.get("path");
		} else {
			ParserGenerator gen = new ParserGenerator(GRAMMAR);
			Parser<Map<String, String>> parser = gen.newParser();
			Map<String, String> vals = parser.parse(urlString);
			protocol = vals.get("protocol");
			String hostname = vals.get("host");

			int at = hostname.indexOf("@");
			if (at >= 0) {
				String auth = hostname.substring(0, at);
				String[] tok = auth.split(":", 2);
				if (tok.length > 1) {
					username = tok[0];
					password = tok[1];
				} else {
					username = auth;
					password = "";
				}

				host = hostname.substring(at + 1);
			} else {
				host = hostname;
				username = null;
				password = null;
			}

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

			if (urlString.toLowerCase().endsWith(".gz")) {
				log.debug("Opening URL {} as GZIP stream...", urlString);
				return new GZIPInputStream(
						SourceURL.class.getResourceAsStream(getPath()));
			}
			return SourceURL.class.getResourceAsStream(getPath());
		}

		if ("tcp".equalsIgnoreCase(protocol)) {
			TcpConnection con = new TcpConnection(host, port);
			return con.getInputStream();
		}

		try {
			URL url = new URL(this.urlString);

			if (urlString.toLowerCase().endsWith(".gz")) {
				log.debug("Opening URL {} as GZIP stream...", urlString);
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

	public String getUsername() {
		return null;
	}

	public String getPassword() {
		return null;
	}
}