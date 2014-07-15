/**
 * 
 */
package stream.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chris
 * 
 */
public class FilesConnectionTest {

	static Logger log = LoggerFactory.getLogger(FilesConnectionTest.class);

	@Test
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

			ArrayList<Integer> results = new ArrayList<Integer>();

			SequentialFileInputStream fis = new SequentialFileInputStream(dir,
					"file-.*", false);
			fis.setMaxWaitingTime(0L);

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					fis));
			String line = reader.readLine();
			while (line != null) {
				log.info("Line: {}", line);
				results.add(new Integer(line.trim()));
				line = reader.readLine();
			}
			reader.close();

			for (int i = 0; i < 10; i++) {
				if (results.size() < i || (results.get(i).intValue() != i)) {
					Assert.fail("Failed to check item at position " + i
							+ " in result list. Was expecting " + i
							+ ", but found: " + results.get(i));
				}
				Assert.assertEquals(new Integer(i), results.get(i));
			}

		} catch (Exception e) {
			log.error("Error: {}", e.getMessage());
			e.printStackTrace();
		}
		// fail("Not yet implemented");
	}
}
