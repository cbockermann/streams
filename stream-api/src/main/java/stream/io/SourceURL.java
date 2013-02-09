/**
 * 
 */
package stream.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.urls.Connection;
import stream.urls.SSLConnection;
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

	final static Map<String, Class<? extends Connection>> urlProvider = new LinkedHashMap<String, Class<? extends Connection>>();
	static {
		urlProvider.put("ssl", stream.urls.SSLConnection.class);
		urlProvider.put("tcp", stream.urls.TcpConnection.class);
		urlProvider.put("fifo", stream.urls.FIFOConnection.class);
	}

	final static String FILE_GRAMMAR = "%(protocol):%(path)";
	final static String JDBC_GRAMMAR = "jdbc:%(driver):%(target)/%(path)";
	final static String GRAMMAR = "%(protocol)://%(address)/%(path)";

	final URL url;
	final String urlString;

	final String protocol;
	final String host;
	final int port;
	final String path;
	final String username;
	final String password;

	final Map<String, String> parameters = new LinkedHashMap<String, String>();

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
				|| urlString.toLowerCase().startsWith("classpath")
				|| urlString.toLowerCase().startsWith("fifo")) {
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
			String grammar = GRAMMAR;
			if (urlString.toLowerCase().startsWith("jdbc")) {
				grammar = JDBC_GRAMMAR;
			}
			ParserGenerator gen = new ParserGenerator(grammar);
			Parser<Map<String, String>> parser = gen.newParser();
			Map<String, String> vals = parser.parse(urlString);
			protocol = vals.get("protocol");

			String hostname = vals.get("address");

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

				hostname = hostname.substring(at + 1);
			} else {
				username = null;
				password = null;
			}

			int idx = hostname.indexOf(":");
			int port = 80;
			if (idx > 0) {
				host = hostname.substring(0, idx);
				port = Integer.parseInt(hostname.substring(idx + 1));
			} else {
				host = hostname;
				if ("http".equalsIgnoreCase(protocol)) {
					port = 80;
				}

				if ("https".equalsIgnoreCase(protocol)) {
					port = 443;
				}
			}
			this.port = port;

			path = vals.get("path");
		}

		if (path != null && path.indexOf("?") >= 0) {
			String qs = path.substring(path.indexOf("?") + 1);
			log.info("Query string for URL is: {}", qs);
			for (String pv : qs.split("&")) {
				if (pv.indexOf("=") > 0) {
					String[] kv = pv.split("=", 2);
					parameters.put(kv[0], kv[1]);
				} else {
					parameters.put(pv, "1");
				}
			}

			log.info("Parameters are: {}", parameters);
		}
	}

	protected boolean isGzip() {
		if (urlString != null && urlString.toLowerCase().endsWith(".gz"))
			return true;

		if (url != null && url.toString().toLowerCase().endsWith(".gz"))
			return true;

		return false;
	}

	public InputStream openStream() throws IOException {

		InputStream inputStream = createStream();

		if (isGzip()) {
			log.debug("Wrapping stream {} in GZIPInputStream for URL {}",
					inputStream, this);
			return new GZIPInputStream(inputStream);
		}

		return inputStream;
	}

	private InputStream createStream() throws IOException {

		if (this.url != null) {
			return url.openStream();
		}

		for (String proto : urlProvider.keySet()) {
			if (proto.equalsIgnoreCase(protocol)) {
				Class<? extends Connection> clazz = urlProvider.get(proto);
				log.debug("Found url-provider '{}' for URL {}", clazz, this);
				try {
					Constructor<? extends Connection> constructor = clazz
							.getConstructor(SourceURL.class);

					log.debug(
							"Using constructor {} to create new instance of provider {}",
							constructor, clazz);
					Connection con = constructor.newInstance(this);
					return con.connect();
				} catch (NoSuchMethodException nsm) {
					nsm.printStackTrace();
					log.error(
							"Failed to create instance of class {} for URL {}",
							clazz, this);
					throw new IOException(nsm.getMessage());
				} catch (Exception e) {
					e.printStackTrace();
					throw new IOException(e.getMessage());
				}
			}
		}

		if ("stdin".equalsIgnoreCase(protocol)) {
			return System.in;
		}

		if ("classpath".equalsIgnoreCase(protocol)) {
			log.debug("Returning InputStream for classpath resource '{}'",
					getPath());
			return SourceURL.class.getResourceAsStream(getPath());
		}

		if ("tcp".equalsIgnoreCase(protocol)) {
			TcpConnection con = new TcpConnection(this);
			return con.getInputStream();
		}

		if ("ssl".equalsIgnoreCase(protocol)) {
			try {
				SSLConnection ssl = new SSLConnection(this);
				ssl.open();
				return ssl.getInputStream();
			} catch (Exception e) {
				throw new IOException(e.getMessage());
			}
		}

		String theUrl = this.urlString;
		try {

			if (theUrl.startsWith("fifo:")) {

				log.info("Handling FIFO URL pattern...");
				theUrl = theUrl.replace("fifo:", "file:");
				File file = new File(theUrl.replace("file:", ""));
				if (!file.exists()) {
					log.info("Creating new fifo file '{}' with mkfifo", file);
					Process p = Runtime.getRuntime().exec(
							"mkfifo " + file.getAbsolutePath());
					log.info("Waiting for mkfifo to return...");
					int ret = p.waitFor();
					log.info("mkfifo finished: {}", ret);
				} else {
					log.info("Using existing fifo-file '{}'", file);
				}

				if (!file.exists()) {
					throw new IOException(
							"Failed to create/acquire FIFO file '"
									+ file.getAbsolutePath() + "'!");
				}

				log.info("Returning FileInputStream for FIFO {}", file);
				FileInputStream fis = new FileInputStream(file);
				return fis;
			}

			log.info("The URL string is: '{}'", theUrl);
			URL url = new URL(theUrl);

			return url.openStream();
		} catch (Exception e) {
			log.error(
					"Failed to open '{}' with default Java URL mechanism: {}",
					theUrl, e.getMessage());
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

	public Map<String, String> getParameters() {
		return parameters;
	}
}