/**
 * 
 */
package stream.property;

import java.net.URL;

import junit.framework.Assert;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.runtime.ProcessContainer;

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

		Assert.assertEquals(System.getProperty("property"), "test1");
	}

	@Test
	public void testProperty2() throws Exception {

		URL url = PropertiesTest.class.getResource("/test-property2.xml");
		ProcessContainer c = new ProcessContainer(url);
		c.run();

		Assert.assertEquals(System.getProperty("property"), "test2");
	}

	@Test
	public void testProperty3() throws Exception {

		URL url = PropertiesTest.class.getResource("/test-property3.xml");
		System.setProperty("container.default.properties", PropertiesTest.class
				.getResource("/propertyglobaltest.properties").toString());
		System.setProperty("container.default.properties", PropertiesTest.class
				.getResource("/propertytest.properties").toString());
		ProcessContainer c = new ProcessContainer(url);

		c.run();

		Assert.assertEquals(System.getProperty("property"), "test2");
	}
}
