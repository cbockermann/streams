/**
 * 
 */
package stream.io;

import static org.junit.Assert.fail;

import java.net.URL;

import org.junit.Test;

import stream.runtime.ProcessContainer;

/**
 * @author chris
 * 
 */
public class SQLWriterTest {

	@Test
	public void test() {
		try {
			URL url = SQLWriterTest.class
					.getResource("/test-sql-writer.xml");
			ProcessContainer pc = new ProcessContainer(url);
			pc.run();
		} catch (Exception e) {
			fail("Failed to create process-container: " + e.getMessage());
		}
	}

}
