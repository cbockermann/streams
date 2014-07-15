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
package stream.flow;

import java.net.URL;

import junit.framework.Assert;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.runtime.ProcessContainer;
import stream.test.CollectorService;

/**
 * @author chris
 * 
 */
public class BlockingQueueTest {

	static Logger log = LoggerFactory.getLogger(BlockingQueueTest.class);

	@Test
	public void test() throws Exception {

		URL url = BlockingQueueTest.class
				.getResource("/test-blocking-queue.xml");
		ProcessContainer c = new ProcessContainer(url);

		long time = c.execute();
		log.info("Container required {} ms for running.", time);

		CollectorService col = c.getContext().lookup("collected",
				CollectorService.class);
		log.info("Collector service: {}", col);

		int colSize = col.getCollection().size();
		log.info("Number of collected elements: {}", colSize);

		// int cnt = 0;
		// for (Data item : col.getCollection()) {
		// log.info("   {}", item);
		// SequenceID id = (SequenceID) item.get("@source:item");
		// log.info("     => {}",
		// Long.parseLong(id.toString().substring(2), 16));
		// cnt++;
		// }
		// log.info("cnt = {}", cnt);

		Assert.assertEquals(110, colSize);
	}
}
