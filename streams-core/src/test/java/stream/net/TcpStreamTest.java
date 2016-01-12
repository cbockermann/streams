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
package stream.net;

import static org.junit.Assert.fail;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.runtime.ProcessContainer;

/**
 * @author chris
 * 
 */
public class TcpStreamTest {

	static Logger log = LoggerFactory.getLogger(TcpStreamTest.class);

	@Test
	public void test() {

		try {

			RandomDataServer server = new RandomDataServer("127.0.0.1", 100);
			server.start();
			Thread.sleep(1000);
			log.info("Creating server socket at port {}", server.getLocalPort());
			log.info("Server address is {}", server.getLocalAddress());
			log.info("Waiting a second for the server to start... ");

			Map<String, String> props = new HashMap<String, String>();
			props.put("server.address", server.getLocalAddress());
			props.put("server.port", server.getLocalPort() + "");

			URL url = TcpStreamTest.class.getResource("/tcp-stream-test.xml");
			log.info("Starting test-container from {}", url);

			ProcessContainer container = new ProcessContainer(url, null, props);
			log.info("Starting container...");
			container.run();

		} catch (Exception e) {
			e.printStackTrace();
			fail("Error: " + e.getMessage());
		}

	}
}
