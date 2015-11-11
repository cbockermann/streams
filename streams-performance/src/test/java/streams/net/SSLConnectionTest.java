/**
 * 
 */
package streams.net;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import streams.logging.Rlog;

/**
 * @author chris
 *
 */
public class SSLConnectionTest {

	static Logger log = LoggerFactory.getLogger(SSLConnectionTest.class);

	// @Test
	// public void test() {
	//
	// try {
	// Socket socket = SecureConnect.connect("performance.sfb876.de", 443);
	//
	// PrintStream out = new PrintStream(socket.getOutputStream());
	// out.println("GET /index.html HTTP/1.1");
	// out.println("Host: performance.sfb876.de");
	// out.println("");
	// out.flush();
	//
	// BufferedReader reader = new BufferedReader(new
	// InputStreamReader(socket.getInputStream()));
	// String line = reader.readLine();
	// while (line != null) {
	// log.info("IN: {}", line);
	// line = reader.readLine();
	// }
	//
	// out.close();
	// reader.close();
	// socket.close();
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// fail("Test failed: " + e.getMessage());
	// }
	// }

	@Test
	public void testSendMessages() {
		try {
			System.setProperty("rlog.trace", "test");
			System.setProperty("rlog.url",
					"https://performance.sfb876.de/receiver?auth=ab09cfe1d60b602cb7600b5729da939f");
			System.setProperty("rlog.token", "ab09cfe1d60b602cb7600b5729da939f");
			Rlog rlog = new Rlog();
			rlog.define("trace", "test");

			rlog.log("test");

			rlog.message("trace", "test").add("msg", "Hello, world!").add("performance", 45.34).send();

		} catch (Exception e) {
			e.printStackTrace();
			fail("Test failed: " + e.getMessage());
		}
	}

}
