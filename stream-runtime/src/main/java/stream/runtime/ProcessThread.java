/**
 * 
 */
package stream.runtime;

/**
 * @author chris
 * 
 */
public class ProcessThread extends Thread {

	final AbstractProcess process;

	public ProcessThread(AbstractProcess process) {
		this.process = process;
	}

	public boolean isRunning() {
		return process.isRunning();
	}
}
