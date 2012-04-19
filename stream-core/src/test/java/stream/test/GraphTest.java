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
public class GraphTest {

	@Test
	public void test() throws Exception {
		URL url = GraphTest.class.getResource("/graph-test.xml");
		stream.run.main(url);
	}
}
