/**
 * 
 */
package stream.service;

import static org.junit.Assert.fail;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.runtime.rpc.RMIClient;
import stream.runtime.rpc.RMINamingService;

/**
 * @author chris
 * 
 */
public class RMINamingServiceTest {

	static Logger log = LoggerFactory.getLogger(RMINamingServiceTest.class);
	int port = 24254;
	RMINamingService namingService;

	@Before
	public void setup() throws Exception {

		namingService = new RMINamingService("test", "localhost", port, false);

		ReverseStringServiceImpl reverser = new ReverseStringServiceImpl();
		namingService.register("reverse", reverser);
		log.info("Reverse service registered...");
	}

	@Test
	public void test() {

		try {

			log.info("Creating RMIClient to port {}", port);
			RMIClient client = new RMIClient("localhost", port);

			log.info("Looking up service 'reverse'");
			ReverseStringService reverse = client.lookup("reverse",
					ReverseStringService.class);

			log.info("Reverse service is: {}", reverse);

			Assert.assertNotNull(reverse);

			String input = "ABC";
			String exp = "CBA";

			String output = reverse.reverse(input);
			log.info("Result from  echo('{}') = '{}'", input, output);
			Assert.assertEquals(exp, output);

		} catch (Exception e) {
			e.printStackTrace();
			fail("Error: " + e.getMessage());
		}
	}
}
