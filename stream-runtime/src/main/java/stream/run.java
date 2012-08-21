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
package stream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.runtime.ElementHandler;
import stream.runtime.ProcessContainer;
import stream.runtime.StreamRuntime;

/**
 * @author chris
 * 
 */
public class run {

	static Logger log = LoggerFactory.getLogger(stream.run.class);

	public static void setupOutput() throws Exception {
		if (System.getProperty("container.stdout") != null) {
			System.setOut(new PrintStream(new FileOutputStream(System
					.getProperty("container.stdout"))));
		}

		if (System.getProperty("container.stderr") != null) {
			System.setOut(new PrintStream(new FileOutputStream(System
					.getProperty("container.stdout"))));
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		setupOutput();

		URL url;
		try {
			url = new URL(args[0]);
		} catch (Exception e) {
			File f = new File(args[0]);
			url = f.toURI().toURL();
		}
		main(url);
	}

	public static void main(URL url) throws Exception {

		setupOutput();

		StreamRuntime.setupLogging();

		log.info("Creating process-container from configuration at {}", url);
		ProcessContainer container = new ProcessContainer(url);

		log.info("Starting process-container...");
		container.run();
		log.info("Container finished.");
		System.exit(0);
	}

	public static void main(URL url, Map<String, ElementHandler> elementHandler)
			throws Exception {

		StreamRuntime.setupLogging();

		log.info("Creating process-container from configuration at {}", url);
		ProcessContainer container = new ProcessContainer(url, elementHandler);

		log.info("Starting process-container...");
		container.run();
		log.info("Container finished.");
		System.exit(0);
	}

	public static void main(String resource) throws Exception {
		log.info("Looking for configuration at resource {} in classpath",
				resource);
		main(run.class.getResource(resource));
	}

	public static void main(String resource,
			Map<String, ElementHandler> elementHandler) throws Exception {
		log.info("Looking for configuration at resource {} in classpath",
				resource);
		main(run.class.getResource(resource), elementHandler);
	}
}