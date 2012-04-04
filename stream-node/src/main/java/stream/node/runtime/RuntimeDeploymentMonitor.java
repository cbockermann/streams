/**
 * 
 */
package stream.node.runtime;

import java.io.File;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.runtime.ProcessContainer;

/**
 * @author chris
 * 
 */
public class RuntimeDeploymentMonitor extends Thread {

	static Logger log = LoggerFactory.getLogger(RuntimeDeploymentMonitor.class);
	boolean running = true;
	final File deploymentDirectory;
	final RuntimeManager manager;

	public RuntimeDeploymentMonitor(RuntimeManager manager,
			File deploymentDirectory) {
		this.manager = manager;
		this.deploymentDirectory = deploymentDirectory;
	}

	public void shutdown() {
		running = false;
	}

	/**
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		while (running) {
			log.trace("Checking for new deployments...");
			manager.checkDeployments();
			log.trace("Sleeping for 1000ms");
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void checkDeployments() {
		File[] files = deploymentDirectory.listFiles();
		if (files != null) {

			for (File f : files) {
				if (f.getName().endsWith(".xml")) {

					try {
						URL url = f.toURI().toURL();
						log.info(
								"Trying to create process-container from file {}",
								url);

						ProcessContainer pc = new ProcessContainer(url);
						if (pc.getName() == null)
							pc.setName(f.getName().replaceAll("\\.xml", ""));
						log.info("created container '{}'", pc.getName());
						log.info("container is listening for: {}",
								pc.getStreamListenerNames());

						ProcessContainerThread workerThread = new ProcessContainerThread(
								f, pc);

						manager.containers.put(pc.getName(), pc);
						manager.worker.add(workerThread);

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
}
