/**
 * 
 */
package stream.flow;

import static org.junit.Assert.fail;

import java.net.URL;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chris
 * 
 */
public class EnqueueTest {

	static Logger log = LoggerFactory.getLogger(EnqueueTest.class);

	@Test
	public void test() {
		try {
			URL url = EnqueueTest.class.getResource("/enqueue-test.xml");
			stream.run.main(url);

		} catch (Exception e) {
			log.error("Test failed: {}", e.getMessage());
			fail("Test failed: " + e.getMessage());
		}
	}
}
