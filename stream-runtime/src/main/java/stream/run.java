/**
 * 
 */
package stream;

import java.io.File;
import java.net.URL;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.runtime.ElementHandler;
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
		URL url;
		try {
			url = new URL(args[0]);
		} catch (Exception e) {
			File f = new File(args[0]);
			url = f.toURI().toURL();
		}
		main(url);
	}

	public static void main(URL url) throws Exception {
		log.info("Creating process-container from configuration at {}", url);
		ProcessContainer container = new ProcessContainer(url);

		log.info("Starting process-container...");
		container.run();
	}

	public static void main(URL url, Map<String, ElementHandler> elementHandler)
			throws Exception {
		log.info("Creating process-container from configuration at {}", url);
		ProcessContainer container = new ProcessContainer(url, elementHandler);

		log.info("Starting process-container...");
		container.run();
	}

	public static void main(String resource) throws Exception {
		log.info("Looking for configuration at resource {} in classpath",
				resource);
		main(run.class.getResource(resource));
	}

	public static void main(String resource,
			Map<String, ElementHandler> elementHandler) throws Exception {
		log.info("Looking for configuration at resource {} in classpath",
				resource);
		main(run.class.getResource(resource));
	}

}
