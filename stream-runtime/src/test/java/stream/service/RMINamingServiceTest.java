/*
 *  streams library
 *
 *  Copyright (C) 2011-2014 by Christian Bockermann, Hendrik Blom
 * 
 *  streams is a library, API and runtime environment for processing high
 *  volume data streams. It is composed of three submodules "stream-api",
 *  "stream-core" and "stream-runtime".
 *
 *  The streams library (and its submodules) is free software: you can 
 *  redistribute it and/or modify it under the terms of the 
 *  GNU Affero General Public License as published by the Free Software 
 *  Foundation, either version 3 of the License, or (at your option) any 
 *  later version.
 *
 *  The stream.ai library (and its submodules) is distributed in the hope
 *  that it will be useful, but WITHOUT ANY WARRANTY; without even the implied 
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/.
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

		System.setProperty("java.rmi.server.hostname", "127.0.0.1");

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
			ReverseStringService reverse = client.lookup("//test/reverse",
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
