/*
 *  stream.ai
 *
 *  Copyright (C) 2011-2012 by Christian Bockermann, Hendrik Blom
 * 
 *  stream.ai is a library, API and runtime environment for processing high
 *  volume data streams. It is composed of three submodules "stream-api",
 *  "stream-core" and "stream-runtime".
 *
 *  The stream.ai library (and its submodules) is free software: you can 
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
package stream.plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.annotations.Description;
import stream.plugin.monitoring.StreamPlotView;
import stream.plugin.util.ParameterTypeDiscovery;
import stream.plugin.util.ProcessorFinder;

import com.rapidminer.gui.MainFrame;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.tools.OperatorService;

/**
 * @author chris
 * 
 */
public final class DataStreamPlugin {

	static Logger log = LoggerFactory.getLogger(DataStreamPlugin.class);

	static OperatorNamingService namingService = OperatorNamingService
			.getInstance();

	public final static String NAME = "DataStream-Plugin";

	public final static String VERSION = "v0.3";

	public final static String DATA_ITEM_PORT_NAME = "data item";

	public final static String DATA_STREAM_PORT_NAME = "stream";

	final static StreamPlotView streamPlotView = new StreamPlotView();

	final static Set<Class<?>> REGISTERED_PROCESSORS = new HashSet<Class<?>>();
	final static Set<String> IGNORE_LIST = new HashSet<String>();

	static MainFrame mainframe;

	public static boolean inStreamingMode() {

		if ("true".equalsIgnoreCase(System.getProperty("rapidminer.streaming"))) {
			return true;
		}

		if (System.getenv("RAPIDMINER_STREAMING") != null) {
			return true;
		}

		return false;
	}

	public static StreamPlotView getStreamPlotView() {
		return streamPlotView;
	}

	public static void initPlugin() {

		log.info("");
		log.info("Initializing {}, {}", NAME, VERSION);
		log.info("Running in Rapidminer-Streaming-Mode?  {}", inStreamingMode());
		log.info("");
		log.info("Using NamingService {}", OperatorNamingService.getInstance());
		log.info("");
		try {

			/*
			 * URL url = DataStreamPlugin.class
			 * .getResource("/stream/plugin/log4j.properties"); if
			 * ("true".equalsIgnoreCase(System
			 * .getProperty("DataStreamPlugin.debug")) ||
			 * "1".equalsIgnoreCase(System .getenv("DATASTREAM_PLUGIN_DEBUG")))
			 * { url = DataStreamPlugin.class
			 * .getResource("/stream/plugin/log4j-debug.properties"); }
			 * PropertyConfigurator.configure(url);
			 */

			URL url = DataStreamPlugin.class.getResource("/ignore-classes.txt");
			if (url != null) {
				try {
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(url.openStream()));
					String line = reader.readLine();
					while (line != null) {
						IGNORE_LIST.add(line.trim());
						line = reader.readLine();
					}
					reader.close();
				} catch (Exception e) {
					log.error("Failed to read ignore-list from {}: {}", url,
							e.getMessage());
					if (log.isDebugEnabled())
						e.printStackTrace();
				}
			}

			url = DataStreamPlugin.class
					.getResource("/stream/plugin/resources/DataStreamOperators-core.xml");

			OperatorService.registerOperators("DataStream", url.openStream(),
					DataStreamPlugin.class.getClassLoader());

			String[] packages = new String[] { "stream", "fact" };

			URL[] externalJars = new URL[0];

			try {

				List<URL> urls = new ArrayList<URL>();

				File streams = new File(System.getProperty("user.home")
						+ File.separator + ".streams");
				if (streams.isDirectory()) {
					log.info("Found '.streams' directory at {}", streams);
					File[] files = streams.listFiles();
					if (files != null) {
						for (File file : files) {
							if (file.getName().toLowerCase().endsWith(".jar")) {
								log.info(
										"Adding jar '{}' to class loader path...",
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

			URLClassLoader classLoader = URLClassLoader.newInstance(
					externalJars, DataStreamPlugin.class.getClassLoader());

			List<Class<?>> processorClasses = ProcessorFinder.findProcessors(
					packages, classLoader);

			for (Class<?> clazz : processorClasses) {

				if (!GenericOperatorDescription.canCreate(clazz)) {
					continue;
				}

				if (REGISTERED_PROCESSORS.contains(clazz)) {
					log.debug("Operator for processor {} already registered.",
							clazz);
					continue;
				}

				if (IGNORE_LIST.contains(clazz.getName())) {
					log.info(
							"Ignoring class {} as it has been marked as 'ignore'",
							clazz.getName());
					continue;
				} else {
					log.debug(
							"Class {} is not marked as 'ignore', adding it to the list.",
							clazz.getName());
				}

				log.info("Registering operator for processor {}", clazz);
				Map<String, ParameterType> types = ParameterTypeDiscovery
						.discoverParameterTypes(clazz);
				for (String key : types.keySet()) {
					log.debug("   {} = {}", key, types.get(key).getClass());
				}

				Description desc = clazz.getAnnotation(Description.class);
				String key = clazz.getSimpleName();
				if (desc.name() != null && !"".equals(desc.name().trim()))
					key = desc.name();

				String group = desc.group();
				if (group == null) {
					group = clazz.getPackage().getName();
				}

				GenericOperatorDescription sod = new GenericOperatorDescription(
						group, key, clazz,
						DataStreamPlugin.class.getClassLoader(), null, null);

				OperatorService.registerOperator(sod, null);
				REGISTERED_PROCESSORS.add(clazz);
			}

		} catch (Exception e) {
			log.error("Failed to initialized logging: {}", e.getMessage());
			e.printStackTrace();
		}

	}
}
