/**
 * 
 */
package stream.node.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.plugin.OperatorNamingService;
import stream.service.Service;
import stream.service.ServiceInfo;

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

		String services = "";
		try {
			services = printServiceList();
		} catch (Exception e) {
			e.printStackTrace();
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}

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

		try {
			Map<String, ServiceInfo> services = ns.list();
			// Map<String, Processor> serviceNames = ns.getProcessors();
			for (String name : services.keySet()) {
				sb.append("<tr>");
				sb.append("<td><a href=\"/processor/" + name + "\">" + name
						+ "</a></td>");
				try {

					Class<? extends Service>[] sc = services.get(name)
							.getServices();
					Object service = ns.lookup(name, sc[0]);
					sb.append("<td><code>"
							+ service.getClass().getCanonicalName()
							+ "</code></td>");
				} catch (Exception e) {
					sb.append("<td>" + e.getMessage() + "</td>");
				}
				sb.append("</tr>");
			}
			sb.append("</table>");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	protected String printServiceList() throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("<table>");
		sb.append("<tr><th>Name</th><th>Services</tr>");
		Map<String, ServiceInfo> serviceNames = ns.list(); // .getServiceNames();
		for (String name : serviceNames.keySet()) {

			log.info("Service  ( {}, {} )", name, serviceNames.get(name));

			sb.append("<tr>");
			String link = "<td>" + name + "</td>";

			try {

				Class<? extends Service>[] serviceClasses = serviceNames.get(
						name).getServices();
				Object s = ns.lookup(name, serviceClasses[0]);
				log.info("Service is {}", s);
				if (ServiceServlet.canRender((Service) s)) {
					log.info("Yes! we have a renderer for {}", s);
					link = "<td><a href=\"/service/"
							+ name.replaceAll("\\/", "%2F") + "\">" + name
							+ "</a></td>";
				} else {
					log.info("No renderer exists for {}", s);
				}
				sb.append(link);

				sb.append("<td>");
				int services = 0;
				for (Class<? extends Service> interf : serviceClasses) {
					log.info("Service implements interface {}",
							interf.getCanonicalName());
					if (services > 0)
						sb.append("<br/>");
					sb.append("<a href='${context}/documentation/"
							+ interf.getCanonicalName() + "'>");
					sb.append("<code>");
					sb.append(interf.getCanonicalName());
					sb.append("</code>");
					services++;
				}
				sb.append("</td>");

			} catch (Exception e) {
				e.printStackTrace();
			}
			sb.append("</tr>");
		}
		sb.append("</table>");
		return sb.toString();
	}
}