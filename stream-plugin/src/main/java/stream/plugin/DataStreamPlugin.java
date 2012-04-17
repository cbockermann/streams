/**
 * 
 */
package stream.plugin;

import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.PropertyConfigurator;
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

	public final static String NAME = "DataStream-Plugin";

	public final static String VERSION = "v0.3";

	public final static String DATA_ITEM_PORT_NAME = "data item";

	public final static String DATA_STREAM_PORT_NAME = "stream";

	final static StreamPlotView streamPlotView = new StreamPlotView();

	final static Set<Class<?>> REGISTERED_PROCESSORS = new HashSet<Class<?>>();

	static MainFrame mainframe;

	public static boolean inStreamingMode() {

		if ("true"
				.equalsIgnoreCase(System.getProperty("rapidminer.streaming "))) {
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
		try {

			URL url = DataStreamPlugin.class.getResource("/log4j.properties");
			if ("true".equalsIgnoreCase(System
					.getProperty("DataStreamPlugin.debug"))) {
				url = DataStreamPlugin.class
						.getResource("/log4j-debug.properties");
			}

			PropertyConfigurator.configure(url);

			url = DataStreamPlugin.class
					.getResource("/stream/plugin/resources/DataStreamOperators-core.xml");

			OperatorService.registerOperators("DataStream", url.openStream(),
					DataStreamPlugin.class.getClassLoader());

			String[] packages = new String[] { "stream", "fact" };

			List<Class<?>> processorClasses = ProcessorFinder
					.findProcessors(packages);

			for (Class<?> clazz : processorClasses) {

				if (!GenericOperatorDescription.canCreate(clazz)) {
					continue;
				}

				if (REGISTERED_PROCESSORS.contains(clazz)) {
					log.debug("Operator for processor {} already registered.",
							clazz);
					continue;
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

	public static void initGui(MainFrame mf) {
		mainframe = mf;
		mainframe.registerDockable(streamPlotView);
	}

	public static MainFrame getMainFrame() {
		return mainframe;
	}
}
