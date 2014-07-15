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

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chris
 * 
 */
public class JDBCUrlTest {

	static Logger log = LoggerFactory.getLogger(JDBCUrlTest.class);

	@Test
	public void test() {

		try {
			String jdbc = "jdbc:mysql://dbuser:dbpass@localhost:3306/dbname?autoReconnect=true";
			SourceURL url = new SourceURL(jdbc);

			String host = url.getHost();
			log.info("Database host is: {}", url.getHost());
			log.info("Database port is: {}", url.getPort());
			log.info("Database name is: {}", url.getPath());
			log.info("Database user is: {}", url.getUsername());
			log.info("Database password is: {}", url.getPassword());
			log.info("Database parameters are: {}", url.getParameters());
			Assert.assertEquals("localhost", host);

		} catch (Exception e) {
			e.printStackTrace();
			fail("Parsing test of JDBC URL failed: " + e.getMessage());
		}
	}
}
