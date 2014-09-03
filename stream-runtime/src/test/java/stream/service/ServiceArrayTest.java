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

import java.net.URL;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.data.DataFactory;
import stream.io.QueueService;
import stream.runtime.ProcessContainer;

/**
 * @author chris
 * 
 */
public class ServiceArrayTest {

	static Logger log = LoggerFactory.getLogger(ServiceArrayTest.class);

	@Test
	public void test() {

		try {
			URL url = ServiceArrayTest.class
					.getResource("/service-array-test.xml");
			final ProcessContainer pc = new ProcessContainer(url);

			GlobalCollector gc = (GlobalCollector) pc.getContext().lookup(
					"col1", QueueService.class);
			log.info("Global collector-1 is: {}", gc);

			Thread t = new Thread() {
				public void run() {
					try {
						pc.run();
					} catch (Exception e) {
						e.printStackTrace();
						fail("Test failed: " + e.getMessage());
					}
				}
			};
			t.start();

			for (int i = 1; i < 21; i++) {

				Data item = DataFactory.create();
				item.put("@id", i);
				item.put("@stream", "input");
				log.info("Adding item {}", item);

				pc.dataArrived("input", item);
				Thread.sleep(250);
				log.info("Global collection has size: {}", gc.getCollection()
						.size());
				Assert.assertTrue(gc.getCollection().size() % 2 == 0);
			}
			Thread.sleep(500);

			pc.shutdown();

		} catch (Exception e) {
			e.printStackTrace();
			fail("Test failed: " + e.getMessage());
		}
	}

}
