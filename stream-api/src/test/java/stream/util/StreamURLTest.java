/**
 * 
 */
package stream.util;

import static org.junit.Assert.fail;
import junit.framework.Assert;

import org.junit.Test;

import stream.io.SourceURL;

/**
 * @author chris
 * 
 */
public class StreamURLTest {

	@Test
	public void test() {

		try {
			String str = "tcp://192.168.128.1:412";
			SourceURL url = new SourceURL(str);
			Assert.assertNotNull(url);
		} catch (Exception e) {
			fail("Test failed: " + e.getMessage());
		}
	}

}
