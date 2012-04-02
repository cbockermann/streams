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
public class TestCsvStream {

	@Test
	public void test() throws Exception {
		URL url = TestCsvStream.class.getResource("/test-csv.xml");
		ProcessContainer container = new ProcessContainer(url);
		container.run();
	}
}
