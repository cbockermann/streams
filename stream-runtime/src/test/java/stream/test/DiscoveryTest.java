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
package stream.test;

import java.net.URL;
import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.runtime.Controller;
import stream.runtime.DefaultNamingService;
import stream.runtime.rpc.ContainerAnnouncement;
import stream.runtime.rpc.Discovery;
import stream.runtime.rpc.RMIClient;

/**
 * @author chris
 * 
 */
public class DiscoveryTest {

	static Logger log = LoggerFactory.getLogger(DiscoveryTest.class);

	@Test
	public void test() throws Exception {
		System.setProperty("process.multiply", "true");
		URL url = DependencyTest.class.getResource("/example.xml");
		stream.run.main(url);
	}

	public static void main(String[] args) throws Exception {
		// (new DiscoveryTest()).test();

		Discovery discovery = new Discovery();
		discovery.discover();

		DefaultNamingService ns = new DefaultNamingService();

		Map<String, ContainerAnnouncement> ann = discovery.getAnnouncements();
		for (String key : ann.keySet()) {
			log.info(" {} => {}", key, ann.get(key));
			ContainerAnnouncement an = ann.get(key);
			ns.addContainer(key, new RMIClient(an.getHost(), an.getPort()));
		}

		Controller ctrl = ns.lookup("//nico:storage/.ctrl", Controller.class);
		log.info("Controller: {}", ctrl);
		ctrl.shutdown();
	}
}
