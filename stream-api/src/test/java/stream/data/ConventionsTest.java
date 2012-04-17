/**
 * 
 */
package stream.data;

import junit.framework.Assert;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Conventions.Key;

/**
 * @author chris
 * 
 */
public class ConventionsTest {

	static Logger log = LoggerFactory.getLogger(ConventionsTest.class);

	/**
	 * Test method for
	 * {@link stream.data.Conventions#createKey(java.lang.String)}.
	 */
	@Test
	public void testCreateKey() {

		String attribute = "feature";
		Key key = Conventions.createKey(attribute);

		Assert.assertNull(key.annotation);

		Assert.assertTrue(attribute.equals(key.toString()));
	}

	@Test
	public void testCreateAnnotatedKey() {
		String attribute = "@label:play";

		Key key = Conventions.createKey(attribute);

		Assert.assertNotNull(key.annotation);

		Assert.assertTrue(key.annotation.equals("label"));
		Assert.assertTrue(key.name.equals("play"));

	}
}
