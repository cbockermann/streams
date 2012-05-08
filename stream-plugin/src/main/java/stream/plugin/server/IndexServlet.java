/**
 * 
 */
package stream.plugin.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Processor;
import stream.plugin.OperatorNamingService;
import stream.service.Service;

/**
 * @author chris
 * 
 */
public class IndexServlet extends AbstractStreamServlet {

	/** The unique class ID */
	private static final long serialVersionUID = -7612686573328499862L;

	static Logger log = LoggerFactory.getLogger(IndexServlet.class);
	final OperatorNamingService ns = OperatorNamingService.getInstance();

	final Map<String, String> ctx = new HashMap<String, String>();

	public IndexServlet() {
		super("/template.html");
	}

	/**
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		log.info("Service request to {}", req.getRequestURI());
		resp.setContentType("text/html");

		ctx.put("context", req.getContextPath());

		String services = this.printServiceList();

		Template serv = new Template("/templates/services.html");
		ctx.put("content", serv.expand("content", services));

		String html = template.expand(ctx); // ctx.expand(template);
		resp.getWriter().print(html);

		resp.setStatus(HttpServletResponse.SC_OK);
		resp.getWriter().flush();
	}

	protected String printProcessorList() {
		StringBuilder sb = new StringBuilder();
		sb.append("<table>");
		sb.append("<tr><th>Name</th><th>Type</th></tr>");
		Map<String, Processor> serviceNames = ns.getProcessors();
		for (String name : serviceNames.keySet()) {
			sb.append("<tr>");
			sb.append("<td><a href=\"/processor/" + name + "\">" + name
					+ "</a></td>");
			try {
				Processor service = serviceNames.get(name);
				sb.append("<td><code>" + service.getClass().getCanonicalName()
						+ "</code></td>");
			} catch (Exception e) {
				sb.append("<td>" + e.getMessage() + "</td>");
			}
			sb.append("</tr>");
		}
		sb.append("</table>");
		return sb.toString();
	}

	protected String printServiceList() {
		StringBuilder sb = new StringBuilder();
		sb.append("<table>");
		sb.append("<tr><th>Name</th><th>Provider</th><th>Services</tr>");
		Set<String> serviceNames = ns.getServiceNames();
		for (String name : serviceNames) {
			sb.append("<tr>");

			String link = "<td>" + name + "</td>";

			try {
				Service s = ns.lookup(name);
				log.info("Service is {}", s);
				if (ServiceServlet.canRender(s)) {
					log.info("Yes! we have a renderer for {}", s);
					link = "<td><a href=\"/service/" + name + "\">" + name
							+ "</a></td>";
				} else {
					log.info("No renderer exists for {}", s);
				}
			} catch (Exception e) {
				log.error("Failed to look up service '{}': {}", name,
						e.getMessage());
			}
			sb.append(link);
			try {
				Service service = ns.lookup(name);
				sb.append("<td><code>" + service.getClass().getCanonicalName()
						+ "</code></td>");

				sb.append("<td>");
				Class<?>[] intf = service.getClass().getInterfaces();
				int services = 0;
				for (Class<?> interf : intf) {
					log.info("Service implements interface {}",
							interf.getCanonicalName());

					if (interf != Service.class
							&& Service.class.isAssignableFrom(interf)) {
						if (services > 0)
							sb.append("<br/>");
						sb.append("<a href='${context}/documentation/"
								+ interf.getCanonicalName() + "'>");
						sb.append("<code>");
						sb.append(interf.getCanonicalName());
						sb.append("</code>");
						services++;
					}
				}
				sb.append("</td>");

			} catch (Exception e) {
			}
			sb.append("</tr>");
		}
		sb.append("</table>");
		return sb.toString();
	}
}