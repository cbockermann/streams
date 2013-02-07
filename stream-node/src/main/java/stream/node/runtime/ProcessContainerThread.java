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
package stream.node.runtime;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.runtime.ProcessContainer;

/**
 * @author chris
 * 
 */
public class ProcessContainerThread extends Thread {

	static Logger log = LoggerFactory.getLogger(ProcessContainerThread.class);
	File containerFile;
	ProcessContainer processContainer;

	// Process jvm;

	public ProcessContainerThread(File containerFile, ProcessContainer pc) {
		this.containerFile = containerFile;
		this.processContainer = pc;
	}

	/**
	 * @return the processContainer
	 */
	public ProcessContainer getProcessContainer() {
		return processContainer;
	}

	/**
	 * @param processContainer
	 *            the processContainer to set
	 */
	public void setProcessContainer(ProcessContainer processContainer) {
		this.processContainer = processContainer;
	}

	public File getFile() {
		return containerFile;
	}

	/**
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		log.info("Starting container from file {}", containerFile);
		try {
			processContainer.run();
			// runVM(containerFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.info("Container {} finished.", containerFile);
	}

	public void shutdown() {
		if (processContainer != null)
			processContainer.shutdown();
		/*
		 * if (jvm != null) {
		 * log.debug("Sending 'destroy' signal to spawned JVM...");
		 * jvm.destroy(); }
		 */
	}

	public static void runVM(File file) throws Exception {

		log.info("Running container {} in separate JVM", file);

		LinkedHashSet<URL> urls = new LinkedHashSet<URL>();
		Class<?>[] classes = new Class[] { stream.run.class,
				stream.io.CsvWriter.class };

		for (Class<?> cl : classes) {

			URLClassLoader ucl = (URLClassLoader) cl.getClassLoader();
			for (URL url : ucl.getURLs()) {
				urls.add(url);
			}
		}

		// StreamNodeContext.class.getClassLoader();
		StringBuffer classPath = new StringBuffer();

		Iterator<URL> it = urls.iterator();
		while (it.hasNext()) {
			URL url = it.next();
			log.info("   " + url);

			if (url.toURI().getScheme().equalsIgnoreCase("file")) {
				classPath.append((new File(url.toURI().getPath()))
						.getAbsolutePath());
				if (it.hasNext())
					classPath.append(File.pathSeparatorChar);
			}
		}

		log.info("Classpath: " + classPath.toString());

		log.debug("JAVA_HOME = " + System.getenv("JAVA_HOME"));
		for (String key : System.getenv().keySet()) {
			log.trace(key + " = " + System.getenv(key));
		}

		File stdoutFile = new File(file.getAbsolutePath() + ".stdout");
		log.info("Writing jvm output to {}", stdoutFile);
		File stderrFile = new File(file.getAbsolutePath() + ".stderr");

		String exec = System.getProperty("java.home") + File.separator + "bin"
				+ File.separator + "java";
		File java = new File(exec);
		log.info("file " + java + " exists? " + java.exists());

		// Process jvm = Runtime.getRuntime().exec(
		// new String[] { java.getAbsolutePath(), " -cp " + classPath,
		// "stream.run", args[0] });

		ArrayList<String> args = new ArrayList<String>();
		args.add(java.getAbsolutePath());
		args.add("-cp");
		args.add(classPath.toString());
		args.add("-Dcontainer.stdout=" + stdoutFile.getAbsolutePath());
		args.add("-Dcontainer.stderr=" + stderrFile.getAbsolutePath());

		for (Object key : System.getProperties().keySet()) {
			String k = key.toString();
			String opt = "-D" + k + "=" + System.getProperty(k);
			if (!args.contains(opt))
				args.add(opt);
		}

		args.add("stream.Start");
		args.add(file.getAbsolutePath());

		log.info("Command:");
		for (String arg : args) {
			log.info("   {}", arg);
		}

		if (System.getProperty("runtime.exec") != null) {
			log.info("Spawning new JVM with Runtime.getRuntime().exec(...)");
			Process p = Runtime.getRuntime().exec(
					args.toArray(new String[args.size()]));
			log.info("Spawned process by Runtime.exec(...): {}", p);
			return;
		} else {

			log.info("Spawning new JVM with process-builder...");
			ProcessBuilder pb = new ProcessBuilder(args);
			File cwd = file.getParentFile();
			log.info("Working directory is: {}", cwd);
			pb.directory(cwd);
			pb.environment().put("CLASSPATH", classPath.toString());

			log.info("Command: {}", pb.command());
			log.info("Spawning and returning without a reference to the process...");
			pb.start();
		}
	}

	public class LogWriter extends Thread {
		BufferedReader reader;
		PrintStream writer;

		public LogWriter(InputStream input, OutputStream output)
				throws Exception {
			reader = new BufferedReader(new InputStreamReader(input));
			writer = new PrintStream(output);
		}

		public void run() {
			try {
				String line = reader.readLine();
				while (line != null) {
					writer.println(line);
					line = reader.readLine();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}