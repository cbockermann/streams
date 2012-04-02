/**
 * 
 */
package stream.io;

import java.net.URL;

import org.junit.Test;

import stream.runtime.ProcessContainer;

/**
 * @author chris
 * 
 */
public class TestLineStream {

	@Test
	public void test() throws Exception {
		URL url = TestLineStream.class.getResource("/test-line-stream.xml");
		ProcessContainer container = new ProcessContainer(url);
		container.run();
	}
}
