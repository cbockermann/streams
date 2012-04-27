/**
 * 
 */
package stream.io;

import static org.junit.Assert.fail;

import java.net.URL;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.runtime.ProcessContainer;

/**
 * @author chris
 * 
 */
public class SQLWriterTest {

	static Logger log = LoggerFactory.getLogger(SQLWriterTest.class);

	@Test
	public void test() {
		try {
			URL url = SQLWriterTest.class.getResource("/test-sql-writer.xml");
			log.info("Creating process-container from {}", url);
			ProcessContainer pc = new ProcessContainer(url);
			pc.run();
		} catch (Exception e) {
			log.error("Failed to create process-container: {}", e.getMessage());
			fail("Failed to create process-container: " + e.getMessage());
		}
	}

}