/**
 * 
 */
package stream.node.runtime;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.io.DataStreamQueue;
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
	Map<String, DataStreamQueue> queues = new LinkedHashMap<String, DataStreamQueue>();

	RuntimeDeploymentMonitor deploymentMonitor;

	/**
	 * @param name
	 * @param description
	 * @param version
	 */
	public RuntimeManager(File deployDir) {
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
		deploymentMonitor.start();
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

	public void checkDeployments() {
		File[] files = deploymentDirectory.listFiles();
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

						ProcessContainer pc = new ProcessContainer(url);
						if (pc.getName() == null)
							pc.setName(f.getName().replaceAll("\\.xml", ""));
						log.info("created container '{}'", pc.getName());
						log.info("container is listening for: {}",
								pc.getStreamListenerNames());

						ProcessContainerThread workerThread = new ProcessContainerThread(
								f, pc);

						this.containers.put(pc.getName(), pc);
						this.worker.add(workerThread);

						log.info("Starting process-container-thread: {}",
								workerThread);
						workerThread.start();

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
		for (ProcessContainerThread t : worker) {
			if (file.equals(t.getFile())) {
				log.trace("File {} already deployed, run by thread {}", file, t);
				return true;
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
