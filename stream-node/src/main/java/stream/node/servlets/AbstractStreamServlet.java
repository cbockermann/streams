/**
 * 
 */
package stream.node.servlets;

import javax.servlet.http.HttpServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chris
 * 
 */
public abstract class AbstractStreamServlet extends HttpServlet {

	/** The unique class ID */
	private static final long serialVersionUID = -3587820034975178089L;

	static Logger log = LoggerFactory.getLogger(AbstractStreamServlet.class);
	Template template;

	public AbstractStreamServlet(String templateResource) {
		try {
			template = new Template(templateResource);
		} catch (Exception e) {
			log.error("Failed to read template: {}", e.getMessage());
			if (log.isTraceEnabled())
				e.printStackTrace();
		}
		log.debug("Using template:\n{}", template);
	}
}