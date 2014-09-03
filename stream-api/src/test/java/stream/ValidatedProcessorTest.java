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
package stream;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.DataFactory;
import stream.data.ProcessContextMock;

/**
 * @author chris
 * 
 */
public class ValidatedProcessorTest {

	static Logger log = LoggerFactory.getLogger(ValidatedProcessorTest.class);

	@Test
	public void test() {

		VIdentity id = new VIdentity();

		id.setRequires("id:Long".split(","));

		Data item = DataFactory.create();
		item.put("id", 1L);

		try {
			id.init(new ProcessContextMock());
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			log.info("Processing valid item...");
			id.process(item);
		} catch (Exception e) {
			fail("Processing threw exception!");
		}

		try {
			log.info("Processing invalid item...");
			item.remove("id");
			id.process(item);
			fail("Errorneous item not detected!");
		} catch (Exception e) {
			log.info("Validation successfully failed!");
		}
	}
}
