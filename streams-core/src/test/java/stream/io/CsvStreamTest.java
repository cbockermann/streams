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

import static org.junit.Assert.fail;
import junit.framework.Assert;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;

/**
 * @author chris
 * 
 */
public class CsvStreamTest {

	static Logger log = LoggerFactory.getLogger(CsvStreamTest.class);

	@Test
	public void testWithHeader() {

		try {
			SourceURL src = new SourceURL("classpath:/stream1.csv");
			CsvStream stream = new CsvStream(src);
			stream.init();

			Data item = stream.read();
			log.info("item: {}", item);

			Assert.assertTrue(item.containsKey("x")
					&& item.containsKey("stream"));

			stream.close();

		} catch (Exception e) {
			fail("Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Test
	public void testKeys() {

		try {
			SourceURL src = new SourceURL("classpath:/stream1.csv");
			CsvStream stream = new CsvStream(src);
			stream.setHeader(false);
			stream.setKeys("x,stream".split(","));
			stream.init();

			Data item = stream.read();
			log.info("item: {}", item);

			Assert.assertTrue(item.containsKey("x")
					&& item.containsKey("stream"));

			stream.close();

		} catch (Exception e) {
			fail("Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Test
	public void testKeysSkipComments() {

		try {
			SourceURL src = new SourceURL("classpath:/stream2.csv");
			CsvStream stream = new CsvStream(src);
			stream.init();

			Data item = stream.read();
			log.info("item: {}", item);

			Assert.assertFalse(item.containsKey("x")
					&& item.containsKey("stream"));

			stream.close();

		} catch (Exception e) {
			fail("Error: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
