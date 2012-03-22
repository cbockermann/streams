/**
 * 
 */
package stream.plugin;

import java.net.URL;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.plugin.monitoring.StreamPlotView;

import com.rapidminer.gui.MainFrame;

/**
 * @author chris
 * 
 */
public final class DataStreamPlugin {

	static Logger log = LoggerFactory.getLogger(DataStreamPlugin.class);

	public final static String NAME = "DataStream-Plugin";

	public final static String VERSION = "v0.2";

	public final static String DATA_ITEM_PORT_NAME = "data item";

	public final static String DATA_STREAM_PORT_NAME = "stream";

	final static StreamPlotView streamPlotView = new StreamPlotView();

	static MainFrame mainframe;

	public static StreamPlotView getStreamPlotView() {
		return streamPlotView;
	}

	public static void initPlugin() {

		log.info("Initializing {}, {}", NAME, VERSION);
		try {

			URL url = DataStreamPlugin.class.getResource("/log4j.properties");
			if ("true".equalsIgnoreCase(System
					.getProperty("DataStreamPlugin.debug"))) {
				url = DataStreamPlugin.class
						.getResource("/log4j-debug.properties");
			}

			PropertyConfigurator.configure(url);

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
