/**
 * 
 */
package stream.node.servlets;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.node.StreamNodeContext;
import stream.util.URLUtilities;

/**
 * @author chris
 * 
 */
public class PageServlet extends HttpServlet {

	/** The unique class ID */
	private static final long serialVersionUID = 8597955953435706328L;

	static Logger log = LoggerFactory.getLogger(PageServlet.class);
	static Set<String> PAGES = new HashSet<String>();

	String base = "";

	/**
	 * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		base = config.getServletContext().getRealPath(".");
		log.debug("base path is: {}", base);
	}

	/**
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		log.debug("path: {}", req.getRequestURI());
		URL url = null;
		String[] paths = new String[] { "/web" };

		for (String path : paths) {
			String resource = path + req.getRequestURI();
			log.debug("Resource: {}", resource);

			File file = new File(resource);
			if (file.isFile()) {
				log.debug("found file: {}", file);
				url = file.toURI().toURL();
				break;
			}

			url = PageServlet.class.getResource(resource);
			if (url != null)
				break;
		}
		log.debug("resource-url: {}", url);
		if (url == null) {
			log.error("No page found for URL {}", req.getRequestURL());
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		String content = URLUtilities.readContentOrEmpty(url);

		Properties props = new Properties();
		props.setProperty("context.path", req.getContextPath());
		props.setProperty("content", content);
		props.setProperty("stream.node.version", StreamNodeContext.class
				.getPackage().getImplementationVersion() + "");

		String html = HtmlTemplate.expand("/template.html", props);
		resp.getWriter().println(html);
	}
}
