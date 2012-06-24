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
package stream.node;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.node.runtime.RuntimeManager;

/**
 * @author chris
 * 
 */
public class StreamNodeContext implements ServletContextListener {

	static Logger log = LoggerFactory.getLogger(StreamNodeContext.class);
	public static RuntimeManager runtimeManager;

	static File deployDirectory = new File("deployments");

	/**
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextInitialized(ServletContextEvent ctx) {

		log.info("Initializing StreamNodeContext...");
		deployDirectory = new File(ctx.getServletContext().getRealPath(
				"deployments"));

		deployDirectory.mkdirs();

		log.info("Using deployment-directory {}", deployDirectory);
		runtimeManager = RuntimeManager.getInstance(); // (deployDirectory);
		try {
			runtimeManager.start();
		} catch (Exception e) {
			log.error("Failed to start RuntimeManager: {}", e.getMessage());
		}
	}

	/**
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextDestroyed(ServletContextEvent ctx) {
		log.info("shutting down StreamNodeContext...");
		try {
			runtimeManager.stop();
		} catch (Exception e) {
			log.error(e.getMessage());
			if (log.isDebugEnabled())
				e.printStackTrace();
		}
	}

	public static File getDeployDirectory() {
		return deployDirectory;
	}

	public static Map<String, String> getSystemInfo() {
		Map<String, String> info = new LinkedHashMap<String, String>();

		info.put("DataNode User", System.getProperty("user.name"));
		info.put("stream-api Version", "0.9.4-SNAPSHOT"); // TODO: fix this to
															// use dynamic
															// version lookup
		// Version.getVersion("org.jwall", "stream-api").getVersion());
		info.put("Java VM", System.getProperty("java.vm.name"));
		info.put("Java VM Vendor", System.getProperty("java.vm.vendor"));
		info.put("Java VM Version", System.getProperty("java.version"));
		info.put("Operating System", System.getProperty("os.name") + ", "
				+ System.getProperty("os.version"));
		info.put("System Architecture", System.getProperty("os.arch"));
		return info;
	}
}
