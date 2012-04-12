/**
 * 
 */
package stream.logic;

import java.net.URL;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.runtime.ProcessContainer;

/**
 * @author chris
 * 
 */
public class IfTest {

	static Logger log = LoggerFactory.getLogger(IfTest.class);

	@Test
	public void test() throws Exception {
		URL url = IfTest.class.getResource("/test-if.xml");
		ProcessContainer pc = new ProcessContainer(url);
		pc.run();
		// fail("Not yet implemented");
	}
}
