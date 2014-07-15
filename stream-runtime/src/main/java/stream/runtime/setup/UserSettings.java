/*
 *  streams library
 *
 *  Copyright (C) 2011-2014 by Christian Bockermann, Hendrik Blom
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
package stream.runtime.setup;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chris
 * 
 */
public class UserSettings {

	static Logger log = LoggerFactory.getLogger(UserSettings.class);
	public final static String SETTINGS_FILE = "settings.properties";

	final Properties properties;

	public UserSettings() {
		properties = this.findUserSettings();
	}

	public File getStreamsDirectory() {
		return new File(System.getProperty("user.home") + File.separator
				+ ".streams");
	}

	public List<URL> getLibrarySearchPath() {

		List<URL> paths = new ArrayList<URL>();

		try {
			paths.add(getStreamsDirectory().toURI().toURL());
			File streamsLib = getUserLocalFile("lib");
			paths.add(streamsLib.toURI().toURL());
		} catch (Exception e) {
			log.error("Failed to add URL to libary-search-path: {}",
					e.getMessage());
		}

		log.debug("User libary-search-paths: {}", paths);
		return paths;
	}

	public File getUserLocalFile(String name) {
		return new File(getStreamsDirectory().getAbsolutePath()
				+ File.separator + name);
	}

	private Properties findUserSettings() {

		Properties props = new Properties();

		File streamsDir = getStreamsDirectory();
		if (streamsDir.isDirectory()) {
			File file = new File(streamsDir.getAbsolutePath() + File.separator
					+ SETTINGS_FILE);
			if (file.isFile()) {
				addProperties(file);
			}
		}

		return props;
	}

	public void addProperties(File file) {
		try {
			Properties props = new Properties();
			props.load(new FileInputStream(file));
			properties.putAll(props);
		} catch (Exception e) {
			log.error("Failed to add properties from file {}: {}", file,
					e.getMessage());
			if (log.isDebugEnabled())
				e.printStackTrace();
		}
	}

	public String getProperty(String key) {
		return properties.getProperty(key);
	}

	public void setProperty(String key, String value) {
		properties.setProperty(key, value);
	}

	public Properties getProperties() {
		return properties;
	}
}