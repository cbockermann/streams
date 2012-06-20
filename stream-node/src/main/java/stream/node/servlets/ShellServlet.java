package stream.node.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ShellServlet extends HttpServlet {

	/** The unique class ID */
	private static final long serialVersionUID = -6954325828920752674L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		final Map<String, String> params = new HashMap<String, String>();
		params.put("context", req.getContextPath());
		params.put("context.path", req.getContextPath());
		params.put( "content", "<div id='WebShell'></div>" );
		params.put("stream.node.version", "0.9.4-SNAPSHOT");

		Template template = new Template( "/template.html" );
		String html = template.expand( params );
		
		resp.getWriter().write( html );
		resp.getWriter().flush();
	}
}