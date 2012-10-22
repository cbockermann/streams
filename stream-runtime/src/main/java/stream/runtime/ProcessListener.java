/**
 * 
 */
package stream.runtime;

/**
 * @author chris
 * 
 */
public interface ProcessListener {

	public void processStarted(AbstractProcess p);

	public void processFinished(AbstractProcess p);
}
