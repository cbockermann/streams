/**
 * 
 */
package stream.node.servlets;

import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chris
 * 
 */
public class DataStreamServer {

	static Logger log = LoggerFactory.getLogger(DataStreamServer.class);
	final Server server;
	final ServletContextHandler context;

	public DataStreamServer(int port) {
		server = new Server();

		HashLoginService hls = new HashLoginService("DataStreamPlugin",
				"/users.properties");

		SelectChannelConnector connector = new SelectChannelConnector();
		connector.setHost("127.0.0.1");
		connector.setPort(port);

		connector.addBean(hls);

		server.addConnector(connector);

		// server.addBean(hls);
		context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		context.addBean(hls);
		context.addServlet(IndexServlet.class, "/");
		context.addServlet(ServiceServlet.class, "/service/*");
		context.addServlet(DocServlet.class, "/documentation/*");

		ClassPathResourceHandler cprh = new ClassPathResourceHandler();

		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { cprh, context,
				new DefaultHandler() });

		server.setHandler(handlers);
	}

	public void start() {
		try {
			server.start();
		} catch (Exception e) {
			log.error("Failed to start internal jetty: {}", e.getMessage());
			if (log.isDebugEnabled())
				e.printStackTrace();
		}
	}

	public void stop() {
		try {
			server.stop();
		} catch (Exception e) {
			log.error("Failed to stop internal jetty: {}", e.getMessage());
			if (log.isDebugEnabled())
				e.printStackTrace();
		}
	}
}
