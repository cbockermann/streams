/**
 * 
 */
package stream.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author chris
 * 
 */
public class SizeOfTest {

	/**
	 * Test method for {@link stream.util.SizeOf#sizeOf(java.lang.Object)}.
	 */
	@Test
	public void testSizeOf() {
		Assert.assertTrue(2.0 == SizeOf.sizeOf('c'));
	}

	@Test
	public void testSizeOfArray() {
		int len = 1024;
		int[] array = new int[len];
		Assert.assertTrue(4 * len == SizeOf.sizeOf(array));
	}

	@Test
	public void testSizeOfString() {
		Assert.assertTrue(4.0 == SizeOf.sizeOf("ABCD"));
	}

}
