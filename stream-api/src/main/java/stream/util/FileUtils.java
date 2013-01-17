/**
 * 
 */
package stream.util;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chris
 * 
 */
public class FileUtils {

	static Logger log = LoggerFactory.getLogger(FileUtils.class);

	public static File mkfifo(File f) throws Exception {

		File file = f;
		if (!file.exists()) {
			log.info("Creating new fifo file '{}' with mkfifo", file);
			Process p = Runtime.getRuntime().exec(
					"mkfifo " + file.getAbsolutePath());
			log.info("Waiting for mkfifo to return...");
			int ret = p.waitFor();
			log.info("mkfifo finished: {}", ret);
		} else {
			log.info("Using existing fifo-file '{}'", file);
		}

		if (!file.exists()) {
			throw new IOException("Failed to create/acquire FIFO file '"
					+ file.getAbsolutePath() + "'!");
		}

		log.debug("Created fifo {}", file);
		return file;
	}
}
