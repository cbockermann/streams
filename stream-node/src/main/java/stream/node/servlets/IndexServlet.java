/**
 * 
 */
package stream.node.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Processor;
import stream.node.StreamNodeContext;

/**
 * @author chris
 * 
 */
public class IndexServlet extends AbstractStreamServlet {

	/** The unique class ID */
	private static final long serialVersionUID = -7612686573328499862L;

	static Logger log = LoggerFactory.getLogger(IndexServlet.class);

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
		log.debug("Service request to {}", req.getRequestURI());
		resp.setContentType("text/html");

		ctx.put("context", req.getContextPath());
		ctx.put("context.path", req.getContextPath());

		if (req.getRequestURI().startsWith("/index.html")) {
			log.debug("serving status-page...");
			ctx.put("content",
					this.createList(StreamNodeContext.getSystemInfo()));
			Properties p = new Properties();
			p.putAll(ctx);
			String html = HtmlTemplate.expand("/template.html", p);
			resp.getWriter().print(html);
			resp.getWriter().flush();
			return;
		}

		String services = this.printServiceList();

		Template serv = new Template("/templates.html");
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
		Map<String, Processor> serviceNames = new LinkedHashMap<String, Processor>(); // ns.getProcessors();
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

		sb.append("</table>");
		return sb.toString();
	}

	public String createList(Map<String, String> list) {
		StringBuilder sb = new StringBuilder();
		sb.append("<table>\n");
		sb.append("<tr><th>Key</th><th>Value</tr>\n");

		for (String key : list.keySet()) {
			sb.append("<tr><td>" + key + "</td><td>" + list.get(key)
					+ "</td></tr>\n");
		}

		sb.append("</table>");
		return sb.toString();
	}
}
