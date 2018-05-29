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
package stream.property;

import java.net.URL;

import junit.framework.Assert;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.runtime.ProcessContainer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author chris, Hendrik
 *
 */
public class PropertiesTest {

	static Logger log = LoggerFactory.getLogger(PropertiesTest.class);

	@Test
	public void testProperty1() throws Exception {

		URL url = PropertiesTest.class.getResource("/test-property1.xml");
		ProcessContainer c = new ProcessContainer(url);
		c.run();

		assertEquals(System.getProperty("property"), "test1");
	}

	@Test
	public void testProperty2() throws Exception {

		URL url = PropertiesTest.class.getResource("/test-property2.xml");
		ProcessContainer c = new ProcessContainer(url);
		c.run();

		assertEquals(System.getProperty("property"), "test2");
	}

	@Test
	public void testProperty3() throws Exception {
		URL url = PropertiesTest.class.getResource("/test-property3.xml");
		System.setProperty("container.default.properties", PropertiesTest.class
				.getResource("/propertyglobaltest.properties").toString());
		System.setProperty("container.specific.properties",
				PropertiesTest.class.getResource("/propertytest.properties")
						.toString());
		ProcessContainer c = new ProcessContainer(url);

		c.run();

		assertEquals(System.getProperty("property"), "test2");
	}

	@Test
	public void testPropertyEmpty() throws Exception {

		URL url = PropertiesTest.class.getResource("/test-propertyEmpty.xml");
		ProcessContainer c = new ProcessContainer(url);

		assertTrue(c.getVariables().containsKey("property.empty"));
		assertEquals("", c.getVariables().get("property.empty"));

		c.run();

		assertEquals("Expected empty String", "", System.getProperty("property"));
	}
}
