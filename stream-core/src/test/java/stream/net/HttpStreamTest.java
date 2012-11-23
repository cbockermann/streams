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

/**
 * @author chris
 * 
 */
public class HttpStreamTest {

	static Logger log = LoggerFactory.getLogger(HttpStreamTest.class);

	@Test
	public void test() {

		try {

			URL url = HttpStreamTest.class.getResource("/http-stream-test.xml");
			log.info("Starting test-container from {}", url);

			ProcessContainer container = new ProcessContainer(url);
			log.info("Starting container...");
			container.run();

		} catch (Exception e) {
			if (log.isDebugEnabled()) {
				e.printStackTrace();
			}
			fail("Error: " + e.getMessage());
		}

	}
}
