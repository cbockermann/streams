/**
 * 
 */
package stream;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.runtime.ProcessContainer;

/**
 * @author chris
 * 
 */
public class run {

	static Logger log = LoggerFactory.getLogger(stream.run.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		URL url = new URL(args[0]);
		main(url);
	}

	public static void main(URL url) throws Exception {
		log.info("Creating process-container from configuration at {}", url);
		ProcessContainer container = new ProcessContainer(url);

		log.info("Starting process-container...");
		container.run();
	}

	public static void main(String resource) throws Exception {
		log.info("Looking for configuration at resource {} in classpath",
				resource);
		main(run.class.getResource(resource));
	}
}
