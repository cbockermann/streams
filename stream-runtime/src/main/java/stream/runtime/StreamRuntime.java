/**
 * 
 */
package stream.runtime;

import java.io.File;
import java.lang.reflect.Method;

/**
 * @author chris
 * 
 */
public class StreamRuntime {

	public static void setupLogging() {
		File logProp = new File("log4j.properties");
		if (logProp.canRead()) {

			try {
				Class<?> configurator = Class
						.forName("org.apache.log4j.PropertyConfigurator");
				Method configure = configurator.getMethod("configure",
						String.class);
				configure.invoke(null, logProp.getAbsolutePath());
			} catch (Exception e) {
				System.err
						.println("Failed to setup logging with log4j.properties: "
								+ e.getMessage());
			}
		}
	}
}
