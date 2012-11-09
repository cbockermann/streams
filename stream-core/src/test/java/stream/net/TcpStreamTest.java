/**
 * 
 */
package stream.net;

import static org.junit.Assert.fail;

import java.net.URL;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.runtime.ProcessContainer;
import stream.urls.RandomDataServer;
import stream.urls.StreamsURLStreamHandlerFactory;

/**
 * @author chris
 * 
 */
public class TcpStreamTest {

	static Logger log = LoggerFactory.getLogger(TcpStreamTest.class);

	@Test
	public void test() {

		try {

			URL.setURLStreamHandlerFactory(new StreamsURLStreamHandlerFactory());

			RandomDataServer server = new RandomDataServer(100);
			server.start();
			log.info("Creating server socket at port {}", server.getLocalPort());

			System.setProperty("server.address", server.getLocalAddress());
			System.setProperty("server.port", server.getLocalPort() + "");

			URL url = TcpStreamTest.class.getResource("/tcp-stream-test.xml");
			log.info("Starting test-container from {}", url);

			ProcessContainer container = new ProcessContainer(url);
			log.info("Starting container...");
			container.run();

		} catch (Exception e) {
			e.printStackTrace();
			fail("Error: " + e.getMessage());
		}

	}
}
