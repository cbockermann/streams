/**
 * 
 */
package stream.io;

import java.io.FileNotFoundException;
import java.net.URL;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chris
 * 
 */
public class NonExistingFile {

	static Logger log = LoggerFactory.getLogger(NonExistingFile.class);

	@Test
	public void test() {
		try {
			URL url = NonExistingFile.class
					.getResource("/file-not-existing.xml");
			stream.run.main(url);
		} catch (FileNotFoundException e) {
			log.info("Caught expected exception!");
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Unexpected exception!");
		}
	}
}
