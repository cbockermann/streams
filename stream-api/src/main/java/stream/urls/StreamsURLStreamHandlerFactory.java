/**
 * 
 */
package stream.urls;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chris
 * 
 */
public class StreamsURLStreamHandlerFactory implements URLStreamHandlerFactory {

	static Logger log = LoggerFactory
			.getLogger(StreamsURLStreamHandlerFactory.class);

	/**
	 * @see java.net.URLStreamHandlerFactory#createURLStreamHandler(java.lang.String)
	 */
	@Override
	public URLStreamHandler createURLStreamHandler(String proto) {
		log.info("Creating URL stream handler for '{}'", proto);

		if (proto.equalsIgnoreCase("tcp")) {
			log.info("Using custom TCP URL stream handler for protocol '{}'",
					proto);
			return new TcpURLStreamHandler();
		}

		log.info("Using default URLStreamHandler...");
		return new DefaultURLStreamHandler();
	}

	public class DefaultURLStreamHandler extends URLStreamHandler {

		/**
		 * @see java.net.URLStreamHandler#openConnection(java.net.URL)
		 */
		@Override
		protected URLConnection openConnection(URL url) throws IOException {
			return url.openConnection();
		}
	}
}
