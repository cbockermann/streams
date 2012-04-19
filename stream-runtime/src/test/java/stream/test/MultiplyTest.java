/**
 * 
 */
package stream.test;

import java.net.URL;

import org.junit.Test;

/**
 * @author chris
 * 
 */
public class MultiplyTest {

	@Test
	public void test() throws Exception {
		System.setProperty("process.multiply", "true");
		URL url = MultiplyTest.class.getResource("/multiply-test.xml");
		stream.run.main(url);
	}
}
