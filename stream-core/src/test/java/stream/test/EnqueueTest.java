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
public class EnqueueTest {

	@Test
	public void test() throws Exception {
		URL url = MultiplyTest.class.getResource("/test-enqueue.xml");
		stream.run.main(url);
	}
}
