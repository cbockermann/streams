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

import java.net.URL;

import junit.framework.Assert;

import org.junit.Test;

import stream.Data;

public class SvmLightDataStreamTest {

	@Test
	public void testReadNextData() throws Exception {

		try {
			URL url = SvmLightDataStreamTest.class
					.getResource("/test-data.svm_light");
			Stream stream = new SvmLightStream(new SourceURL(url));
			stream.init();
			Data item = stream.read();
			while (item != null) {
				Double expTarget = -1.0d;
				Assert.assertEquals(expTarget.doubleValue(),
						((Double) item.get("@label")).doubleValue(), 0.0001);
				Assert.assertEquals(new Double("0.43"), item.get("1"));
				Assert.assertEquals(new Double("0.12"), item.get("3"));
				Assert.assertEquals(new Double("0.2"), item.get("9284"));
				item = stream.read();
			}

		} catch (Exception e) {
			fail("Error: " + e.getMessage());
		}
	}
}
