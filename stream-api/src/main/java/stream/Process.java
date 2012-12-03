/**
 * 
 */
package stream;

import java.util.List;

import stream.io.Sink;
import stream.io.Source;
import stream.runtime.LifeCycle;

/**
 * <p>
 * This interface defines an abstract process. A process is an active element
 * that will continuously read from a source and emit processed items to a sink
 * (if such a sink is attached).
 * </p>
 * 
 * @author Christian Bockermann, Hendrik Blom
 * 
 */
public interface Process extends LifeCycle {

	/**
	 * The data source of this process.
	 * 
	 * @param The
	 *            data source of this process.
	 */
	public void setSource(Source ds);

	/**
	 * The data source of this process.
	 * 
	 * @return The data source of this process.
	 */
	public Source getSource();

	/**
	 * 
	 * @param sink
	 */
	public void setSink(Sink sink);

	/**
	 * 
	 * @return
	 */
	public Sink getSink();

	public void add(Processor p);

	public void remove(Processor p);

	public List<Processor> getProcessors();

	public void execute() throws Exception;
}