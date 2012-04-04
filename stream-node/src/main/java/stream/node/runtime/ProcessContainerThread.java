/**
 * 
 */
package stream.node.runtime;

import java.io.File;

import stream.runtime.ProcessContainer;

/**
 * @author chris
 * 
 */
public class ProcessContainerThread extends Thread {
	File containerFile;
	ProcessContainer processContainer;

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
		try {
			processContainer.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void shutdown() {
		processContainer.shutdown();
	}
}