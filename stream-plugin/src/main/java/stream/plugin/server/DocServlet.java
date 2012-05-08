/**
 * 
 */
package stream.plugin.server;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.util.URLUtilities;

import com.petebevin.markdown.MarkdownProcessor;

/**
 * @author chris
 * 
 */
public class DocServlet extends AbstractStreamServlet {

	/** The unique class ID */
	private static final long serialVersionUID = -7524454448251203446L;

	static Logger log = LoggerFactory.getLogger(DocServlet.class);

	/**
	 * @param templateResource
	 */
	public DocServlet() {
		super("/template.html");
	}

	/**
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		Map<String, String> params = new HashMap<String, String>();
		params.put("context", req.getContextPath());

		log.info("Handing request to {}", req.getRequestURI());
		String prefix = req.getContextPath() + req.getServletPath();
		String resource = req.getRequestURI().substring(prefix.length());
		log.info("Resource requested is: '{}'", resource);

		if (resource.endsWith("/"))
			resource += "index.md";

		URL url = null;
		if (resource.endsWith(".md") || resource.matches("^/\\w+(\\.\\w+)+")) {

			String[] tests = new String[] { ".md", "/index.md" };

			for (String test : tests) {
				String res = resource.replace('.', '/') + test;
				url = DocServlet.class.getResource(res);
				if (url != null) {
					log.info("Found markdown-documentation at {}", url);
					break;
				}
			}
		}

		if (url == null) {
			String path = "/web/documentation" + resource;
			log.info("Checking for resource '{}' in classpath at '{}'",
					resource, path);
			url = DocServlet.class.getResource(path);
		}

		if (url != null) {
			try {
				String txt = URLUtilities.readContent(url);

				MarkdownProcessor markdown = new MarkdownProcessor();
				String html = markdown.markdown(txt);
				params.put("content", "<div class='documentation'>" + html
						+ "</div>");

				resp.getWriter().print(template.expand(params));
				return;
			} catch (Exception e) {
				resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				log.error("Failed to serve content from resource at {}: {}",
						url, e.getMessage());
				e.printStackTrace();
			}

		} else {
			log.error("No resource found!");
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}
}