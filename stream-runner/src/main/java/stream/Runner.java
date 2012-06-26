/**
 * 
 */
package stream;

import java.awt.Desktop;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.security.ProtectionDomain;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.runtime.StreamRuntime;
import stream.runtime.rpc.RMINamingService;
import stream.util.MD5;

/**
 * @author chris
 * 
 */
public class Runner {

	static Logger log = LoggerFactory.getLogger(Runner.class);

	public Runner() {

	}

	public static void jetty() throws Exception {

		String defaultPort = RMINamingService.getFreePort() + "";
		int port = Integer.parseInt(System.getProperty("port", defaultPort));
		Server server = new Server(port);

		ProtectionDomain domain = Runner.class.getProtectionDomain();
		URL location = domain.getCodeSource().getLocation();

		WebAppContext webapp = new WebAppContext();
		webapp.setContextPath("/");
		webapp.setDescriptor(location.toExternalForm() + "/WEB-INF/web.xml");
		webapp.setServer(server);
		webapp.setWar(location.toExternalForm());

		// (Optional) Set the directory the war will extract to.
		// If not set, java.io.tmpdir will be used, which can cause problems
		// if the temp directory gets cleaned periodically.
		// Your build scripts should remove this directory between deployments
		webapp.setTempDirectory(new File("/tmp" + File.separator
				+ MD5.md5("" + System.currentTimeMillis())));

		server.setHandler(webapp);
		server.start();

		if (Desktop.isDesktopSupported()) {
			Desktop desk = Desktop.getDesktop();
			desk.browse(new URI("http://" + "localhost" + ":" + port));
		} else {
			log.error("Desktop is not supported.");
		}

		server.join();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		StreamRuntime.setupLogging();
		jetty();
	}
}
