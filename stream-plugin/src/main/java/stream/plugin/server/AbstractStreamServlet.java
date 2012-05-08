/**
 * 
 */
package stream.plugin.server;

import javax.servlet.http.HttpServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chris
 * 
 */
public abstract class AbstractStreamServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3587820034975178089L;
	static Logger log = LoggerFactory.getLogger(AbstractStreamServlet.class);
	Template template;

	public AbstractStreamServlet(String templateResource) {
		try {
			template = new Template(templateResource);
		} catch (Exception e) {
			log.error("Failed to read template: {}", e.getMessage());
			e.printStackTrace();
		}
		log.info("Using template:\n{}", template);
	}
}