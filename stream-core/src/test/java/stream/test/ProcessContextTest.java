/**
 * 
 */
package stream.test;

import java.net.URL;

import org.junit.Test;

import stream.runtime.ProcessContainer;

/**
 * @author chris
 * 
 */
public class ProcessContextTest {

	@Test
	public void test() throws Exception {

		URL url = ProcessContextTest.class
				.getResource("/process-context-test.xml");
		ProcessContainer container = new ProcessContainer(url);
		container.run();
	}

}
