/**
 * 
 */
package stream.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chris
 * 
 */
public class FilesConnectionTest {

	static Logger log = LoggerFactory.getLogger(FilesConnectionTest.class);

	// @Test
	public void test() {

		try {
			File dir = File.createTempFile("test", ".d");
			dir.delete();

			dir.mkdirs();
			if (!dir.isDirectory()) {
				log.error("Failed to create directory {}", dir);
				Assert.fail("Failed to create directory " + dir + "!");
			}

			for (int i = 0; i < 10; i++) {
				File fi = new File(dir.getAbsolutePath() + File.separator
						+ "file-" + i);
				FileWriter w = new FileWriter(fi);
				w.write(i + "\n");
				w.close();
				log.info("Write file {}", fi);
				fi.deleteOnExit();
			}

			SequentialFileInputStream fis = new SequentialFileInputStream(dir,
					"file-.*", false);
			fis.maxWaitingTime = 0L;

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					fis));
			String line = reader.readLine();
			while (line != null) {
				log.info("Line: {}", line);
				line = reader.readLine();
			}
			reader.close();

		} catch (Exception e) {
			log.error("Error: {}", e.getMessage());
			e.printStackTrace();
		}
		// fail("Not yet implemented");
	}
}
