/**
 * 
 */
package stream.service;

import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import stream.runtime.rpc.RMINamingService;

/**
 * @author chris
 * 
 */
public class RMINamingServiceTest {

	RMINamingService namingService;

	@Before
	public void setup() throws Exception {

		namingService = new RMINamingService();

		ReverseStringServiceImpl reverser = new ReverseStringServiceImpl();
		namingService.register("reverse", reverser);
	}

	@Test
	public void test() {

		fail("Not yet implemented");
	}
}
