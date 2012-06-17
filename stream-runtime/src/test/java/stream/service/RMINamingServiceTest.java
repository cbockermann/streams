/**
 * 
 */
package stream.service;

import static org.junit.Assert.fail;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import stream.runtime.rpc.RMIClient;
import stream.runtime.rpc.RMINamingService;

/**
 * @author chris
 * 
 */
public class RMINamingServiceTest {

	int port = 14254;
	RMINamingService namingService;

	@Before
	public void setup() throws Exception {

		namingService = new RMINamingService("test", "localhost", port);

		ReverseStringServiceImpl reverser = new ReverseStringServiceImpl();
		namingService.register("reverse", reverser);
	}

	@Test
	public void test() {

		try {

			RMIClient client = new RMIClient(port);

			ReverseStringService reverse = client.lookup("reverse",
					ReverseStringService.class);
			Assert.assertNotNull(reverse);

			String input = "ABC";
			String exp = "CBA";

			String output = reverse.reverse(input);
			Assert.assertEquals(exp, output);

		} catch (Exception e) {
			e.printStackTrace();
			fail("Error: " + e.getMessage());
		}
	}
}
