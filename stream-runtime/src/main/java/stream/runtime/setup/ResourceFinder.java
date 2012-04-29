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
package stream.runtime.setup;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chris
 * 
 */
public class ResourceFinder {

	static Logger log = LoggerFactory.getLogger(ResourceFinder.class);

	public static String[] getResource(Condition c) throws Exception {
		return getResources("", c);
	}

	/**
	 * Scans all classes accessible from the context class loader which belong
	 * to the given package and subpackages.
	 * 
	 * @param packageName
	 *            The base package
	 * @return The classes
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public static String[] getResources(String packageName, Condition cond)
			throws ClassNotFoundException, IOException {
		ArrayList<String> classes = new ArrayList<String>();
		ClassLoader classLoader = ResourceFinder.class.getClassLoader(); // .getContextClassLoader();
		assert classLoader != null;
		String path = packageName; // .replace('.', '/');
		Enumeration<URL> resources = classLoader.getResources(path);
		List<File> dirs = new ArrayList<File>();
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();

			if (resource.toString().startsWith("jar:")) {
				log.info("Scanning jar-file {}", resource.getPath());

				String p = resource.getPath();
				if (p.indexOf("!") > 0) {
					p = p.substring(0, p.indexOf("!"));
					log.info("Opening jar '{}'", p);

					if (p.startsWith("file:"))
						p = p.substring("file:".length());

					classes.addAll(findResources(new JarFile(p), packageName,
							cond));
				}

			} else {
				log.trace("Checking URL {}", resource);
				dirs.add(new File(resource.getFile()));
			}
		}
		for (File directory : dirs) {
			classes.addAll(findResources(directory, packageName, cond));
		}
		return classes.toArray(new String[classes.size()]);
	}

	public static List<String> findResources(JarFile jar, String packageName,
			Condition cond) throws ClassNotFoundException {
		List<String> classes = new ArrayList<String>();
		log.trace("Checking jar-file {}", jar.getName());
		Enumeration<JarEntry> en = jar.entries();
		while (en.hasMoreElements()) {

			JarEntry entry = en.nextElement();
			entry.getName();
			log.trace("Checking JarEntry '{}'", entry.getName());

			if (cond.matches(entry.getName())) {
				try {
					log.trace("Adding entry {}", entry);
					classes.add(entry.getName());
				} catch (Exception e) {
					log.error("Failed to load class for entry '{}'",
							entry.getName());
				}
			}

		}

		return classes;
	}

	/**
	 * Recursive method used to find all classes in a given directory and
	 * subdirs.
	 * 
	 * @param directory
	 *            The base directory
	 * @param packageName
	 *            The package name for classes found inside the base directory
	 * @return The classes
	 * @throws ClassNotFoundException
	 */
	private static List<String> findResources(File directory,
			String packageName, Condition cond) throws ClassNotFoundException {

		log.trace("Searching directory '{}' for package '{}'", directory,
				packageName);

		List<String> classes = new ArrayList<String>();
		if (!directory.exists()) {
			return classes;
		}
		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				classes.addAll(findResources(file,
						packageName + "/" + file.getName(), cond));
			} else if (cond.matches(packageName + "/" + file.getName())) {
				try {
					log.trace("Adding entry '{}' (package is: {})",
							file.getAbsolutePath(), directory);
					classes.add(packageName + "/" + file.getName());
				} catch (Exception e) {
					log.error("Failed to add class: {}", e.getMessage());
				}
			}
		}
		return classes;
	}

	public interface Condition {
		public boolean matches(String path);
	}

	public final static Condition isMarkdown = new Condition() {
		@Override
		public boolean matches(String path) {
			return path != null && path.toLowerCase().endsWith(".md");
		}
	};
}
