/**
 * 
 */
package stream;

import java.awt.Desktop;
import java.net.URI;

import org.eclipse.jetty.server.Server;

/**
 * @author chris
 * 
 */
public class Runner {

	Server server;

	public Runner() {

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		Server server = new Server();

		if (Desktop.isDesktopSupported()) {
			Desktop desk = Desktop.getDesktop();
			desk.browse(new URI("http://www.jwall.org/"));
		} else {
			System.out.println("Desktop is not supported.");
		}
	}
}
