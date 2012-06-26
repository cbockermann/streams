/**
 * 
 */
package com.rapidminer.beans;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.annotations.Description;
import stream.plugin.FakePlugin;
import stream.plugin.GenericOperatorDescription;
import stream.plugin.StreamsPlugin;
import stream.util.URLUtilities;

import com.rapidminer.annotations.OperatorInfo;
import com.rapidminer.beans.utils.ClassFinder;
import com.rapidminer.tools.OperatorService;

/**
 * @author chris
 * 
 */
public class RapidMinerBeans {

	static Logger log = LoggerFactory.getLogger(RapidMinerBeans.class);
	public final static Set<Class<?>> REGISTERED_PROCESSORS = new HashSet<Class<?>>();
	public final static Set<String> IGNORE_LIST = new HashSet<String>();

	static String beanDirectory = System.getProperty("user.home")
			+ File.separator + ".RapidMiner5" + File.separator + "beans";

	final static List<URL> urls = new ArrayList<URL>();

	static {
		checkLibs();
	}

	private static void checkLibs() {
		try {
			urls.clear();
			URL[] externalJars = new URL[0];

			File beansDirectory = new File(beanDirectory);
			if (beansDirectory.isDirectory()) {
				log.info("Found 'beans' directory at {}", beansDirectory);
				File[] files = beansDirectory.listFiles();
				if (files != null) {
					for (File file : files) {
						if (file.getName().toLowerCase().endsWith(".jar")) {
							log.info("Adding jar '{}' to class loader path...",
									file);
							urls.add(file.toURI().toURL());
						}
					}
				}
			}

			if (!urls.isEmpty()) {
				externalJars = new URL[urls.size()];
				for (int i = 0; i < urls.size(); i++) {
					externalJars[i] = urls.get(i);
					log.debug("Using extra jar {}", externalJars[i]);
				}
			} else {
				log.info("No extra-jars found!");
			}

		} catch (Exception e) {
			log.error("Failed to add custom jars: {}", e.getMessage());
			if (log.isDebugEnabled())
				e.printStackTrace();
		}
	}

	public static void findAndRegisterBeans() {
		findAndRegisterBeans("RapidMiner-Beans", "1.0", "rmx_beans");
	}

	public static void findAndRegisterBeans(String pluginName, String version,
			String namespace) {
		log.debug("findAndRegisterBeans()");
		String[] packages = new String[] { "", "stream", "fact" };

		URLClassLoader classLoader = URLClassLoader.newInstance(
				urls.toArray(new URL[urls.size()]),
				RapidMinerBeans.class.getClassLoader());

		Set<Class<?>> processorClasses = ClassFinder.getClasses(packages,
				classLoader);
		log.info("Found {} potential classes in {}", processorClasses.size(),
				urls);
		for (Class<?> clazz : processorClasses) {

			if (!clazz.isAnnotationPresent(Description.class)
					&& !clazz.isAnnotationPresent(OperatorInfo.class)) {
				log.debug("Skipping non-annotated class {}",
						clazz.getCanonicalName());
				continue;
			}

			if (!GenericOperatorDescription.canCreate(clazz)) {
				log.info("Cannot create operator for {}", clazz.getSimpleName());
				continue;
			}

			if (REGISTERED_PROCESSORS.contains(clazz)) {
				log.info("Operator for processor {} already registered.", clazz);
				continue;
			}

			if (IGNORE_LIST.contains(clazz.getName())) {
				log.info("Ignoring class {} as it has been marked as 'ignore'",
						clazz.getName());
				continue;
			} else {
				log.debug(
						"Class {} is not marked as 'ignore', adding it to the list.",
						clazz.getName());
			}

			log.info("Registering operator for class '{}'",
					clazz.getCanonicalName());
			Description desc = clazz.getAnnotation(Description.class);
			String key = clazz.getSimpleName();
			if (desc != null && desc.name() != null
					&& !"".equals(desc.name().trim()))
				key = desc.name();

			String group = clazz.getPackage().getName();
			if (desc != null) {
				group = desc.group();
			}
			log.info("   group: {}", group);
			group = group.replace("Data Stream.", "Streams.");
			log.info("   renamed group: {}", group);

			GenericOperatorDescription sod = new GenericOperatorDescription(
					group, key, clazz, StreamsPlugin.class.getClassLoader(),
					null, FakePlugin.createPlugin(pluginName, version,
							namespace));

			try {
				OperatorService.registerOperator(sod, null);
				REGISTERED_PROCESSORS.add(clazz);
			} catch (Exception e) {
				log.error("Failed to register operator for class {}: {}",
						clazz, e.getMessage());
				e.printStackTrace();
			}
		}

		log.info("{} generic RapidMinerBeans registered.",
				REGISTERED_PROCESSORS.size());
	}

	public static void loadIgnoreList(URL url) {

		if (url == null) {
			return;
		}

		String list = URLUtilities.readContentOrEmpty(url);

		for (String line : list.split("\n+")) {
			if (!line.trim().startsWith("#") && !"".equals(line.trim())) {
				log.info("Adding '{}' to ignore-list...", line.trim());
				IGNORE_LIST.add(line.trim());
			}
		}
	}

}
