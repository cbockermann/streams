/**
 * 
 */
package stream.data;

import junit.framework.Assert;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;

/**
 * @author chris
 * 
 */
public class CreateCopyTest {

	static Logger log = LoggerFactory.getLogger(CreateCopyTest.class);

	/**
	 * Test method for {@link stream.data.DataImpl#createCopy()}.
	 */
	@Test
	public void testCreateCopy() {

		Data orig = new DataImpl();
		orig.put("a", 1.0);

		Data copy = orig.createCopy();
		copy.put("a", 2.0);
		copy.put("b", 2.0);

		log.info("orig: {}", orig);
		log.info("copy: {}", copy);

		Assert.assertTrue(new Double(1.0).equals((Double) orig.get("a")));
		Assert.assertTrue(new Double(2.0).equals((Double) copy.get("a")));

		// fail("Not yet implemented");
	}

}
