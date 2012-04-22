/**
 * 
 */
package stream.runtime;

import java.io.File;
import java.lang.reflect.Method;

import stream.runtime.setup.UserSettings;

/**
 * @author chris
 * 
 */
public class StreamRuntime {

	public final static UserSettings userSettings = new UserSettings();

	public static String[] logSearchPath = new String[] { "",
			userSettings.getStreamsDirectory().getAbsolutePath() };

	public static void setupLogging() {

		for (String path : logSearchPath) {
			String p = path;
			if (!p.isEmpty())
				p = path + File.separator + "log4j.properties";
			else
				p = "log4j.properties";

			File logProp = new File(p);
			if (logProp.canRead()) {
				System.err.println("Using log settings from "
						+ logProp.getAbsolutePath());
				try {
					Class<?> configurator = Class
							.forName("org.apache.log4j.PropertyConfigurator");
					Method configure = configurator.getMethod("configure",
							String.class);
					configure.invoke(null, logProp.getAbsolutePath());
					break;
				} catch (Exception e) {
					System.err
							.println("Failed to setup logging with log4j.properties: "
									+ e.getMessage());
				}
			}
		}
	}
}
