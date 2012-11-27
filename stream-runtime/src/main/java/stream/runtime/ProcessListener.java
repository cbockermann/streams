/**
 * 
 */
package stream.runtime;

/**
 * @author chris
 * 
 */
public interface ProcessListener {

	public void processStarted(stream.Process p);

	public void processFinished(stream.Process p);
}
