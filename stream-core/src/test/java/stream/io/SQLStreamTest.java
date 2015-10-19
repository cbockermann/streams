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

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.runtime.ProcessContextImpl;
import stream.test.TestStream;

/**
 * @author chris
 * 
 */
public class SQLStreamTest {

	static Logger log = LoggerFactory.getLogger(SQLStreamTest.class);
	String dbUrl = "jdbc:hsqldb:res:/test.db";
	String dbUser = "SA";
	String dbPass = "";

	@Before
	public void setup() throws Exception {

		dbUrl = "jdbc:hsqldb:res:/test.db";
		log.info("Creating test database at url {}", dbUrl);

		TestStream stream = new TestStream();

		SQLWriter writer = new SQLWriter();
		writer.setUrl(dbUrl);
		writer.setUsername(dbUser);
		writer.setPassword(dbPass);
		writer.setTable("TEST_TABLE");

		writer.init(new ProcessContextImpl("0"));

		for (int i = 0; i < 100; i++) {
			Data item = stream.readNext();
			writer.process(item);
		}

		writer.finish();

	}

	/**
	 * Test method for {@link stream.io.AbstractStream#read()}.
	 */
	@Test
	public void testReadNext() {

		try {
			SQLStream stream = new SQLStream();
			stream.setUrl(new SourceURL(dbUrl));
			stream.setUsername(dbUser);
			stream.setPassword(dbPass);
			stream.setSelect("SELECT * FROM TEST_TABLE");

			stream.init();

			Data item = stream.read();
			while (item != null) {
				log.info("Read item: {}", item);
				item = stream.read();
			}

			stream.close();

		} catch (Exception e) {
			e.printStackTrace();
			fail("Error: " + e.getMessage());
		}
	}

}
