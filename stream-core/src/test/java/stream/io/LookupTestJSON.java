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
package stream.io;

import java.net.URL;

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
public class LookupTestJSON {

	static Logger log = LoggerFactory.getLogger(LookupTestJSON.class);

	@Test
	public void test() throws Exception {

		URL url = LookupTestJSON.class.getResource("/lookup-test-csv.xml");

		ProcessContainer pc = new ProcessContainer(url);
		long time = pc.execute();
		log.info("Container finished after {} ms.", time);

		CollectorService collector = pc.getContext().lookup("collector",
				CollectorService.class);
		log.info("Collector service is: {}", collector);
		Assert.assertNotNull(collector);

		for (Data item : collector.getCollection()) {

			Assert.assertNotNull(item.get("id"));
			Assert.assertNotNull(item.get("name"));

			String id = item.get("id").toString();

			if ("chris".equals(id)) {
				Assert.assertTrue("Christian Bockermann".equals(item
						.get("name")));
			}

			if ("hendrik".equals(id)) {
				Assert.assertTrue("Hendrik Blom".equals(item.get("name")));
			}
		}
	}
}