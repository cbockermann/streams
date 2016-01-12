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
package stream;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chris
 * 
 */
public class Start {

	static Logger log = LoggerFactory.getLogger(Start.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		URLClassLoader ucl = (URLClassLoader) Start.class.getClassLoader();
		StringBuffer classPath = new StringBuffer();

		URL[] urls = ucl.getURLs();
		for (int i = 0; i < urls.length; i++) {
			System.out.println("   " + urls[i]);

			if (urls[i].toURI().getScheme().equalsIgnoreCase("file")) {
				classPath.append((new File(urls[i].toURI().getPath()))
						.getAbsolutePath());
				if (i + 1 < urls.length)
					classPath.append(File.pathSeparatorChar);
			}
		}

		System.out.println("Classpath: " + classPath.toString());

		System.out.println("JAVA_HOME = " + System.getenv("JAVA_HOME"));
		for (String key : System.getenv().keySet()) {
			System.out.println(key + " = " + System.getenv(key));
		}

		System.out
				.println("------------------------------------------------------");
		for (Object key : System.getProperties().keySet()) {
			System.out.println(key + " = " + System.getProperty(key + ""));

		}

		String exec = System.getProperty("java.home") + File.separator + "bin"
				+ File.separator + "java";
		File java = new File(exec);
		System.out.println("file " + java + " exists? " + java.exists());

		// Process jvm = Runtime.getRuntime().exec(
		// new String[] { java.getAbsolutePath(), " -cp " + classPath,
		// "stream.run", args[0] });
		ArrayList<String> cmd = new ArrayList<String>();
		cmd.add(java.getAbsolutePath());
		for (Object key : System.getProperties().keySet()) {
			if (key.toString().startsWith("container")
					|| key.toString().startsWith("stream")) {
				cmd.add("-D" + key.toString() + "="
						+ System.getProperty(key.toString()));
			}
		}
		cmd.add("stream.run");
		cmd.add(args[0]);

		String[] c = cmd.toArray(new String[cmd.size()]);

		ProcessBuilder pb = new ProcessBuilder(c);
		/*
		 * java.getAbsolutePath(), "-Dcontainer.stdout=/tmp/mem-log.xml.stdout",
		 * "-Dcontainer.stderr=/tmp/mem-log.xml.stderr", "stream.run", args[0]);
		 */

		pb.environment().put("CLASSPATH", classPath.toString());

		log.info("Command: {}", pb.command());
		java.lang.Process jvm = pb.start();

		if (jvm.hashCode() > 0) {
			log.info("Not monitoring spawned JVM... exiting...");
			return;
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				jvm.getInputStream()));
		String line = reader.readLine();
		while (line != null) {
			System.out.println(line);
			line = reader.readLine();
		}
		int code = jvm.waitFor();
		reader.close();
		System.out.println("exit code: " + code);
	}
}
