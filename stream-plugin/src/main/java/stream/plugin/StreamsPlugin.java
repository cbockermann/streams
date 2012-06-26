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

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.node.servlets.DataStreamServer;
import stream.plugin.monitoring.StreamPlotView;

import com.rapidminer.beans.RapidMinerBeans;
import com.rapidminer.gui.MainFrame;

/**
 * @author chris
 * 
 */
public final class StreamsPlugin {

	static Logger log = LoggerFactory.getLogger(StreamsPlugin.class);

	static OperatorNamingService namingService = OperatorNamingService
			.getInstance();

	public final static String NAME = "RapidMiner-Streams";

	public final static String VERSION = StreamsPlugin.class.getPackage()
			.getImplementationVersion();

	public final static String URL = "http://www.jwall.org/streams/stream-plugin/";

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

	private static void initLogging() {
		URL url = StreamsPlugin.class
				.getResource("/stream/plugin/log4j.properties");
		if ("true".equalsIgnoreCase(System
				.getProperty("DataStreamPlugin.debug"))
				|| "1".equalsIgnoreCase(System
						.getenv("DATASTREAM_PLUGIN_DEBUG"))) {
			url = StreamsPlugin.class
					.getResource("/stream/plugin/log4j-debug.properties");
		}
		if (url != null)
			PropertyConfigurator.configure(url);
	}

	public static void initPlugin() {
		try {
			initLogging();
			log.info("");
			log.info("Initializing {}, {}", NAME, VERSION);
			System.setProperty("stream.plugin.name", NAME);
			System.setProperty("stream.plugin.version", VERSION);
			System.setProperty("stream.plugin.url", URL);
			log.info("Running in Rapidminer-Streaming-Mode?  {}",
					inStreamingMode());
			log.info("");
			log.info("Using NamingService {}",
					OperatorNamingService.getInstance());
			log.info("");

			URL url = StreamsPlugin.class.getResource("/ignore-classes.txt");
			log.info("Using ignore-list {}", url);
			RapidMinerBeans.loadIgnoreList(url);

			RapidMinerBeans.findAndRegisterBeans("Streams-Plugin", VERSION,
					"rmx_streams");

			startServer();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected static void startServer() {
		int port = 9080;
		log.info("Creating DataStreamServer at port {}", port);
		DataStreamServer server = new DataStreamServer(port);
		log.info("Starting DataStreamServer...");
		server.start();
	}
}
