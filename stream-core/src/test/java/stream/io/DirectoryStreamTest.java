/**
 * 
 */
package stream.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.URL;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chris
 * 
 */
public class DirectoryStreamTest {

	static Logger log = LoggerFactory.getLogger(DirectoryStreamTest.class);

	@Test
	public void test() throws Exception {

		File dummy = File.createTempFile("directory-stream", "");
		dummy.deleteOnExit();
		File dir = new File(dummy.getAbsolutePath() + ".d");

		dir.mkdirs();
		for (int i = 0; i < 10; i++) {
			File file = new File(dir.getAbsolutePath() + File.separator
					+ "test-file-" + i + ".txt");
			write("Test data for file " + i, file);
			log.info("Created test-file {}", file);
			file.deleteOnExit();
		}

		int i = 0;
		for (File file : dir.listFiles()) {
			log.info("  file[{}] = {}", i, file);
			i++;
		}

		dir.deleteOnExit();

		URL url = DirectoryStreamTest.class
				.getResource("/directory-stream-test.xml");
		System.setProperty("file_url", "file:" + dir.getAbsolutePath());

		stream.run.main(url);

		// fail("Not yet implemented");
	}

	private void write(String msg, File file) throws Exception {
		PrintStream p = new PrintStream(new FileOutputStream(file));
		p.print(msg);
		p.close();
	}
}
