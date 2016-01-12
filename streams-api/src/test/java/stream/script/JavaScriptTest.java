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
package stream.script;

import static org.junit.Assert.fail;

import java.io.File;
import java.net.URL;

import junit.framework.Assert;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.ProcessContext;
import stream.data.DataFactory;
import stream.data.ProcessContextMock;

/**
 * @author chris
 * 
 */
public class JavaScriptTest {

	static Logger log = LoggerFactory.getLogger(JavaScriptTest.class);

	@Test
	public void test() {

		URL url = JavaScriptTest.class.getResource("/test.js");

		JavaScript script = new JavaScript();
		script.setFile(new File(url.getFile()));

		try {
			ProcessContext ctx = new ProcessContextMock();
			ctx.set("stamp-value", "ProcessContextMock object");
			script.init(ctx);

			Data item = DataFactory.create();
			item.put("@timestamp", System.currentTimeMillis());

			log.info("initial item: {}", item);
			item = script.process(item);
			log.info("processed item: {}", item);

			item.clear();

			item = script.process(item);

			Assert.assertNotNull(item.get("count"));
			Assert.assertEquals("2.0", item.get("count").toString());

			log.info("2nd time: {}", item);

		} catch (Exception e) {
			fail("Error: " + e.getMessage());
		}
	}
}
