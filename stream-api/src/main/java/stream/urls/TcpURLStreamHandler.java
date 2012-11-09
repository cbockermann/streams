/**
 * 
 */
package stream.urls;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chris
 * 
 */
public class TcpURLStreamHandler extends URLStreamHandler {

	static Logger log = LoggerFactory.getLogger(TcpURLStreamHandler.class);

	/**
	 * @see java.net.URLStreamHandler#openConnection(java.net.URL)
	 */
	@Override
	protected URLConnection openConnection(URL url) throws IOException {

		TcpURLConnection tcpCon = new TcpURLConnection(url);
		log.debug("Returning TcpURLConnection for URL {}", url);
		return tcpCon;
	}
}