/**
 * 
 */
package stream.plugin.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
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
public class ClassFinder {

	static Logger log = LoggerFactory.getLogger(ClassFinder.class);

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
	public static Class<?>[] getClasses(String packageName)
			throws ClassNotFoundException, IOException {
		ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
		ClassLoader classLoader = ClassFinder.class.getClassLoader(); // .getContextClassLoader();
		log.debug("Using class-loader {}", classLoader);
		assert classLoader != null;
		List<URL> resources = new ArrayList<URL>();
		String path = packageName.replace('.', '/');
		Enumeration<URL> urlList = classLoader.getResources(path);
		while (urlList.hasMoreElements()) {
			resources.add(urlList.nextElement());
		}

		if (classLoader instanceof URLClassLoader) {
			URLClassLoader ucl = (URLClassLoader) classLoader;
			URL[] urls = ucl.getURLs();
			if (urls != null) {
				for (URL url : urls) {
					log.debug("Adding URL {} from URLClassLoader", url);
					resources.add(url);
				}
			}
		}

		List<File> dirs = new ArrayList<File>();
		for (URL resource : resources) {
			if (resource.toString().startsWith("jar:")
					|| resource.toExternalForm().endsWith(".jar")) {
				log.debug("Scanning jar-file {}", resource.getPath());

				String p = resource.getPath();
				if (p.indexOf("!") > 0) {
					p = p.substring(0, p.indexOf("!"));
					log.trace("Opening jar '{}'", p);
				}
				if (p.startsWith("file:"))
					p = p.substring("file:".length());

				classes.addAll(findClasses(new JarFile(p), packageName));
			} else {
				log.trace("Checking URL {}", resource);
				dirs.add(new File(resource.getFile()));
			}
		}

		for (File directory : dirs) {
			List<Class<?>> cl = findClasses(directory, packageName);
			log.debug("Found {} classes in {}", cl.size(), directory);
			classes.addAll(cl);
		}
		return classes.toArray(new Class[classes.size()]);
	}

	public static List<Class<?>> findClasses(JarFile jar, String packageName)
			throws ClassNotFoundException {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		log.debug("Checking jar-file {}", jar.getName());
		Enumeration<JarEntry> en = jar.entries();
		while (en.hasMoreElements()) {

			JarEntry entry = en.nextElement();
			entry.getName();
			log.debug("Checking JarEntry '{}'", entry.getName());

			if (entry.getName().endsWith(".class")
					&& !entry.getName().startsWith("com.rapidminer")
					&& entry.getName().replaceAll("/", ".")
							.startsWith(packageName)) {
				try {
					String className = entry.getName()
							.replaceAll("\\.class$", "").replaceAll("/", ".");
					log.trace("Class-name is: '{}'", className);
					Class<?> clazz = Class.forName(className);

					log.trace("Found class {}", clazz);
					classes.add(clazz);

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
	private static List<Class<?>> findClasses(File directory, String packageName)
			throws ClassNotFoundException {

		if (packageName.startsWith("com.rapidminer"))
			return new ArrayList<Class<?>>();

		log.debug("Searching directory '{}' for package '{}'", directory,
				packageName);

		List<Class<?>> classes = new ArrayList<Class<?>>();
		if (!directory.exists()) {
			return classes;
		}
		File[] files = directory.listFiles();
		if (files == null)
			return classes;

		for (File file : files) {
			if (file.isDirectory()) {
				assert !file.getName().contains(".");

				if (packageName.isEmpty()) {
					classes.addAll(findClasses(file, file.getName()));
				} else {
					classes.addAll(findClasses(file,
							packageName + "." + file.getName()));
				}
			} else if (file.getName().endsWith(".class")) {
				try {
					String className = packageName
							+ '.'
							+ file.getName().substring(0,
									file.getName().length() - 6);

					while (className.startsWith("."))
						className = className.substring(1);

					log.debug("Loading class '{}'", className);
					classes.add(Class.forName(className));
				} catch (Exception e) {
					log.error("Failed to add class: {}", e.getMessage());
				}
			}
		}
		return classes;
	}
}
