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
package stream.data;

import static org.junit.Assert.fail;

import java.net.URL;

import junit.framework.Assert;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.runtime.ProcessContainer;
import stream.service.AssertionService;

/**
 * @author chris
 * 
 */
public class AssertTypesTest {

	static Logger log = LoggerFactory.getLogger(AssertTypesTest.class);

	@Test
	public void test() {
		try {

			URL url = AssertTypesTest.class
					.getResource("/test-assert-types.xml");
			ProcessContainer pc = new ProcessContainer(url);
			pc.run();

			AssertionService assertions = pc.getContext().lookup("assertion1",
					AssertionService.class);
			log.info("Assertions: {}", assertions.getAssertions());
			log.info("Failed assertions: {}", assertions.getAssertionErrors());

			Assert.assertEquals(10, assertions.getAssertions().longValue());
			Assert.assertEquals(0, assertions.getAssertionErrors().longValue());

		} catch (Exception e) {
			e.printStackTrace();
			fail("Test failed: " + e.getMessage());
		}
	}
}