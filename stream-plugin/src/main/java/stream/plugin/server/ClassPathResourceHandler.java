/**
 * 
 */
package stream.plugin.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chris
 * 
 */
public class ClassPathResourceHandler extends AbstractHandler {

	static Logger log = LoggerFactory.getLogger(ClassPathResourceHandler.class);

	final String prefix;

	public ClassPathResourceHandler() {
		this("/web");
	}

	public ClassPathResourceHandler(String prefix) {
		this.prefix = prefix;
	}

	/**
	 * @see org.eclipse.jetty.server.Handler#handle(java.lang.String,
	 *      org.eclipse.jetty.server.Request,
	 *      javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public void handle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		String uri = request.getRequestURI();
		log.debug("Trying to handle request to {}", uri);

		if (uri.endsWith("/")) {
			log.debug("Sorry, I'm not serving directory queries...");
			return;
		}

		String res = prefix + uri;
		log.debug("   Looking up {} in classpath...", res);
		URL url = ClassPathResourceHandler.class.getResource(res);

		log.debug("   Found resource at '{}'", url);
		if (url != null) {
			log.debug("Copying resource data...");
			int bytes = copy(url.openStream(), response.getOutputStream());
			log.debug("{} bytes written.", bytes);
			log.debug("Marking request as handled...");
			baseRequest.setHandled(true);
		}
	}

	public static int copy(InputStream in, OutputStream out) throws IOException {
		byte[] buf = new byte[8192];
		int total = 0;
		int read = in.read(buf);
		while (read > 0) {
			out.write(buf, 0, read);
			total += read;
			read = in.read(buf);
		}
		return total;
	}
}
