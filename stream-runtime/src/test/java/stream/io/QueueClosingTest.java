/**
 * 
 */
package stream.io;

import static org.junit.Assert.fail;

import java.net.URL;

import org.junit.Assert;
import org.junit.Test;

import stream.runtime.ProcessContainer;
import stream.test.ExpectedItems;

/**
 * @author chris
 * 
 */
public class QueueClosingTest {

	@Test
	public void test() {

		try {
			URL url = QueueClosingTest.class
					.getResource("/queue-closing-test.xml");
			ProcessContainer pc = new ProcessContainer(url);
			pc.run();

			Assert.assertTrue(ExpectedItems.finishMethodPerformed.get());

		} catch (Exception e) {
			fail("Error: " + e.getMessage());
		}
	}

}
