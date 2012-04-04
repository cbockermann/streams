/**
 * 
 */
package stream.node.servlets;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.node.StreamNodeContext;

/**
 * @author chris
 * 
 */
public class DeploymentServlet extends HttpServlet {

	/** The unique class ID */
	private static final long serialVersionUID = 1103203634022359997L;

	static Logger log = LoggerFactory.getLogger(DeploymentServlet.class);

	/**
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		log.debug("Processing POST request to {}");
		doPut(req, resp);
	}

	/**
	 * @see javax.servlet.http.HttpServlet#doPut(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		log.debug("Processing file-upload, request-uri: {}",
				req.getRequestURI());
		log.debug("   servlet-context: {}", req.getServletPath());

		String name = req.getRequestURI().substring(
				req.getServletPath().length());
		log.debug("   name = '{}'", name);

		// strip of leading slashes...
		//
		while (name.startsWith("/"))
			name = name.substring(1);

		InputStream in = req.getInputStream();
		File file = new File(StreamNodeContext.getDeployDirectory()
				+ File.separator + name + ".tmp");
		FileOutputStream fos = new FileOutputStream(file);

		byte[] buf = new byte[4096];
		int read = in.read(buf);
		while (read > 0) {
			fos.write(buf, 0, read);
			read = in.read(buf);
		}

		fos.close();
		log.debug("Wrote uploaded data to {}", file);

		File xml = new File(file.getAbsolutePath().replaceAll("\\.tmp", ".xml"));
		log.debug("Renaming file to {}", xml);
		file.renameTo(xml);
	}
}
