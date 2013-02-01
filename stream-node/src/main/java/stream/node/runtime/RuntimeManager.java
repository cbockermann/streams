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

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.io.Queue;
import stream.node.StreamNodeContext;
import stream.runtime.ProcessContainer;

/**
 * @author chris
 * 
 */
public class RuntimeManager {

	static Logger log = LoggerFactory.getLogger(RuntimeManager.class);
	final Map<String, ProcessContainer> containers = new LinkedHashMap<String, ProcessContainer>();
	final List<ProcessContainerThread> worker = new ArrayList<ProcessContainerThread>();
	File deploymentDirectory;
	Map<String, Queue> queues = new LinkedHashMap<String, Queue>();

	RuntimeDeploymentMonitor deploymentMonitor;

	final static RuntimeManager globalRuntimeManager = new RuntimeManager(
			StreamNodeContext.getConfigDirectory());

	public static RuntimeManager getInstance() {
		return globalRuntimeManager;
	}

	/**
	 * @param name
	 * @param description
	 * @param version
	 */
	private RuntimeManager(File deployDir) {
		this.deploymentDirectory = deployDir;
	}

	/**
	 * @see org.jwall.web.audit.console.ConsoleComponent#start(org.jwall.web.audit
	 *      .console.config.Configuration)
	 */
	public void start() throws Exception {

		log.info("Starting ContainerManager");

		if (!deploymentDirectory.isDirectory()) {
			log.debug("Creating deployment directory {}", deploymentDirectory);
			deploymentDirectory.mkdirs();
		}

		deploymentMonitor = new RuntimeDeploymentMonitor(this,
				deploymentDirectory);
		// deploymentMonitor.start();
	}

	/**
	 * @see org.jwall.web.audit.console.ConsoleComponent#stopComponent()
	 */
	public void stop() throws Exception {

		log.info("Stopping ContainerManager...");

		for (ProcessContainerThread pct : worker) {
			log.info("Shutting down process-container thread {}", pct);
			pct.shutdown();
			while (pct.isAlive()) {
				log.info("Waiting for thread {} to finish...", pct);
				pct.join(1000);
			}
		}

	}

	public void deploy(File f) throws Exception {

		log.info("Deploying file {}", f);
		if (isDeployed(f)) {
			throw new Exception("File " + f.getAbsolutePath()
					+ " is already being executed!");
		}

		try {

			File runEnv = new File("/tmp/stream-node/active" + File.separator
					+ f.getName());
			runEnv.mkdirs();
			log.info("Deploying file {} in runtime directory {}", f, runEnv);

			File config = new File(runEnv.getAbsolutePath() + File.separator
					+ "container.xml");
			f.renameTo(config);
			f = config;

			URL url = f.toURI().toURL();
			log.info("Deploying process-container from file {}", url);

			/*
			 * ProcessContainer pc = new ProcessContainer(url); if (pc.getName()
			 * == null) pc.setName(f.getName().replaceAll("\\.xml", ""));
			 * 
			 * log.info("created container '{}'", pc.getName());
			 * log.info("container is listening for: {}",
			 * pc.getStreamListenerNames());
			 * 
			 * ProcessContainerThread workerThread = new
			 * ProcessContainerThread(f, pc);
			 * 
			 * this.containers.put(pc.getName(), pc);
			 * this.worker.add(workerThread); File lock = new
			 * File(f.getAbsolutePath() + ".lock"); lock.createNewFile();
			 * log.info("Starting process-container-thread: {}", workerThread);
			 * workerThread.start();
			 */

			ProcessContainer pc = new ProcessContainer(url);
			ProcessContainerThread t = new ProcessContainerThread(f, pc);

			worker.add(t);
			log.info("Spawning process container {}", f);
			t.start();

			// ProcessContainerThread.runVM(f);

		} catch (Exception e) {
			log.error(
					"Failed to create process-container from {}, error was: {}",
					f, e.getMessage());
			if (log.isDebugEnabled())
				e.printStackTrace();
		}
	}

	public void checkDeployments() {
		log.trace("Checking deployment directory {}", deploymentDirectory);
		File[] files = deploymentDirectory.listFiles();
		log.trace("files: {}", files);
		if (files != null) {

			for (File f : files) {
				if (f.getName().endsWith(".xml")) {

					if (isDeployed(f)) {
						continue;
					}

					try {
						URL url = f.toURI().toURL();
						log.info("Deploying process-container from file {}",
								url);

						deploy(f);
						/*
						 * ProcessContainer pc = new ProcessContainer(url); if
						 * (pc.getName() == null)
						 * pc.setName(f.getName().replaceAll("\\.xml", ""));
						 * log.info("created container '{}'", pc.getName());
						 * log.info("container is listening for: {}",
						 * pc.getStreamListenerNames());
						 * 
						 * ProcessContainerThread workerThread = new
						 * ProcessContainerThread( f, pc);
						 * 
						 * this.containers.put(pc.getName(), pc);
						 * this.worker.add(workerThread);
						 * 
						 * log.info("Starting process-container-thread: {}",
						 * workerThread); workerThread.start();
						 */

					} catch (Exception e) {
						log.error(
								"Failed to create process-container from {}, error was: {}",
								f, e.getMessage());
						if (log.isDebugEnabled())
							e.printStackTrace();
					}
				}
			}

		} else {
			log.debug("No deployments found.");
		}
	}

	public boolean isDeployed(File file) {
		Iterator<ProcessContainerThread> it = worker.iterator();
		while (it.hasNext()) {
			ProcessContainerThread t = it.next();
			if (file.equals(t.getFile()) && t.isAlive()) {
				log.info("File {} already deployed, run by thread {}", file, t);
				return true;
			}

			if (!t.isAlive()) {
				log.info("Removing dead process-container-thread {}", t);
				it.remove();
			}
		}
		return false;
	}

	public ProcessContainer getContainer(String name) {
		log.debug("Returning container '{}', containers: {}", name, containers);
		ProcessContainer pc = containers.get(name);
		log.debug("    container to return is: {}", pc);
		return pc;
	}

	public File getContainerConfig(String name) {
		return new File(deploymentDirectory.getAbsolutePath() + File.separator
				+ name + ".xml");
	}

	public void dataArrived(String container, String input, Data item) {

		ProcessContainer pc = containers.get(container);
		if (pc != null) {
			log.debug("Delegating data item to {}:{}", container, input);
			pc.dataArrived(input, item);
		} else {
			log.debug("No container found for name {}", container);
		}
	}

	public String getDeploymentListXml() {
		StringBuffer s = new StringBuffer();
		s.append("<container-list>\n");

		for (ProcessContainer pc : containers.values()) {
			s.append("   <container-ref>" + pc.toString()
					+ "</container-ref>\n");
		}

		s.append("</container-list>\n");
		return s.toString();
	}
}
