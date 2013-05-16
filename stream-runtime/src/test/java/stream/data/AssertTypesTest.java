/**
 * 
 */
package stream.data;

import static org.junit.Assert.fail;

import java.net.URL;

import junit.framework.Assert;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.runtime.ProcessContainer;
import stream.service.AssertionService;

/**
 * @author chris
 * 
 */
public class AssertTypesTest {

	static Logger log = LoggerFactory.getLogger(AssertTypesTest.class);

	@Test
	public void test() {
		try {

			URL url = AssertTypesTest.class
					.getResource("/test-assert-types.xml");
			ProcessContainer pc = new ProcessContainer(url);
			pc.run();

			AssertionService assertions = pc.getContext().lookup("assertion1",
					AssertionService.class);
			log.info("Assertions: {}", assertions.getAssertions());
			log.info("Failed assertions: {}", assertions.getAssertionErrors());

			Assert.assertEquals(10, assertions.getAssertions().longValue());
			Assert.assertEquals(0, assertions.getAssertionErrors().longValue());

		} catch (Exception e) {
			e.printStackTrace();
			fail("Test failed: " + e.getMessage());
		}
	}
}
