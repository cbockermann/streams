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
package stream.io.multi;

import static org.junit.Assert.fail;

import java.net.URL;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.runtime.ProcessContainer;
import stream.test.CollectorService;

/**
 * @author chris
 * 
 */
public class MultiStreamTest {

	static Logger log = LoggerFactory.getLogger(MultiStreamTest.class);

	@Test
	public void testSingleLeft() {

		try {
			URL url = MultiStreamTest.class
					.getResource("/test-multistream-leftstream.xml");
			ProcessContainer pc = new ProcessContainer(url);
			pc.run();

			CollectorService collect = pc.getContext().lookup("collector",
					CollectorService.class);
			Assert.assertNotNull(collect);
			List<Data> coll = collect.getCollection();
			log.info("Collected {} items: {}", coll.size(), coll);
			Assert.assertEquals(6, coll.size());

		} catch (Exception e) {
			e.printStackTrace();
			fail("Test failed: " + e.getMessage());
		}
	}

	@Test
	public void testSingleRight() {

		try {
			URL url = MultiStreamTest.class
					.getResource("/test-multistream-rightstream.xml");
			ProcessContainer pc = new ProcessContainer(url);
			pc.run();

			CollectorService collect = pc.getContext().lookup("collector",
					CollectorService.class);
			Assert.assertNotNull(collect);
			List<Data> coll = collect.getCollection();
			log.info("Collected {} items: {}", coll.size(), coll);
			Assert.assertEquals(6, coll.size());

		} catch (Exception e) {
			e.printStackTrace();
			fail("Test failed: " + e.getMessage());
		}
	}

	@Test
	public void test() {

		try {
			URL url = MultiStreamTest.class
					.getResource("/test-multistream.xml");
			ProcessContainer pc = new ProcessContainer(url);
			pc.run();

			CollectorService collect = pc.getContext().lookup("collector",
					CollectorService.class);
			Assert.assertNotNull(collect);
			List<Data> coll = collect.getCollection();
			log.info("Collected {} items: {}", coll.size(), coll);
			Assert.assertEquals(12, coll.size());

		} catch (Exception e) {
			e.printStackTrace();
			fail("Test failed: " + e.getMessage());
		}
	}

}
