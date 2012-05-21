/**
 * 
 */
package stream.plugin.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.learner.MetaDataService;
import stream.plugin.OperatorNamingService;
import stream.server.renderer.MetaDataServiceRenderer;
import stream.server.renderer.ServiceRenderer;
import stream.service.Service;

/**
 * @author chris
 * 
 */
public class ServiceServlet extends AbstractStreamServlet {

	/** The unique class ID */
	private static final long serialVersionUID = -2749417392339033810L;

	static Logger log = LoggerFactory.getLogger(ServiceServlet.class);

	@SuppressWarnings("rawtypes")
	final static Map<Class<?>, ServiceRenderer> renderer = new HashMap<Class<?>, ServiceRenderer>();
	static {
		renderer.put(MetaDataService.class, new MetaDataServiceRenderer());
	}

	public static boolean canRender(Service service) {

		Class<?>[] intf = service.getClass().getInterfaces();
		for (Class<?> interf : intf) {
			log.info("Service implements interface {}",
					interf.getCanonicalName());

			if (renderer.containsKey(interf)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @param templateResource
	 */
	public ServiceServlet() {
		super("/template.html");

	}

	/**
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		Map<String, String> ctx = new HashMap<String, String>();

		log.info("Processing request for {}", req.getRequestURI());
		String prefix = req.getContextPath() + req.getServletPath();
		log.info("prefix is: {}", prefix);

		String name = req.getRequestURI().substring(prefix.length());
		while (name.startsWith("/"))
			name = name.substring(1);

		log.info("service is would be: '{}'", name);

		try {
			Service service = OperatorNamingService.getInstance().lookup(name);
			Class<?>[] intf = service.getClass().getInterfaces();
			for (Class<?> interf : intf) {
				log.info("Service implements interface {}",
						interf.getCanonicalName());

				if (renderer.containsKey(interf)) {
					log.info("Found renderer for service {}", name);

					ServiceRenderer render = renderer.get(interf);
					String html = render.renderToHtml(name, service);
					ctx.put("content", html);
					break;
				}
			}
		} catch (Exception e) {
			log.error("Failed to lookup service '{}': {}", name, e.getMessage());
			e.printStackTrace();
		}

		String html = template.expand(ctx);
		resp.getWriter().print(html);
	}
}