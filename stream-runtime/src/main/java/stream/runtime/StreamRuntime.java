/*
 *  streams library
 *
 *  Copyright (C) 2011-2012 by Christian Bockermann, Hendrik Blom
 * 
 *  streams is a library, API and runtime environment for processing high
 *  volume data streams. It is composed of three submodules "stream-api",
 *  "stream-core" and "stream-runtime".
 *
 *  The streams library (and its submodules) is free software: you can 
 *  redistribute it and/or modify it under the terms of the 
 *  GNU Affero General Public License as published by the Free Software 
 *  Foundation, either version 3 of the License, or (at your option) any 
 *  later version.
 *
 *  The stream.ai library (and its submodules) is distributed in the hope
 *  that it will be useful, but WITHOUT ANY WARRANTY; without even the implied 
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/.
 */
package stream.runtime;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.runtime.setup.UserSettings;

/**
 * @author chris
 * 
 */
public class StreamRuntime {

	static Logger log = LoggerFactory.getLogger(StreamRuntime.class);

	public final static UserSettings userSettings = new UserSettings();
	private static boolean loggingSetup = false;

	public static String[] logSearchPath = new String[] { "",
			userSettings.getStreamsDirectory().getAbsolutePath() };

	public static void setupLogging() {

		if (loggingSetup)
			return;

		loggingSetup = true;

		List<String> searchPaths = new ArrayList<String>();
		if (System.getenv("STREAMS_HOME") != null)
			searchPaths.add(System.getenv("STREAMS_HOME") + File.separator
					+ "conf");

		for (String path : logSearchPath)
			searchPaths.add(path);

		for (String path : searchPaths) {
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

	public static void loadUserProperties() {
		File file = new File(System.getProperty("user.home") + File.separator
				+ ".streams.properties");
		try {

			if (file.canRead()) {
				Properties p = new Properties();
				p.load(new FileInputStream(file));

				for (Object k : p.keySet()) {
					log.debug("Adding property '{}' = '{}'", k,
							p.getProperty(k.toString()));
					System.setProperty(k.toString(),
							p.getProperty(k.toString()));
				}
			} else {
				log.debug("No $HOME/.streams.properties file found!");
			}

		} catch (Exception e) {
			log.error("Failed to read properties from {}: {}", file,
					e.getMessage());
			if (log.isDebugEnabled())
				e.printStackTrace();
		}
	}
}
