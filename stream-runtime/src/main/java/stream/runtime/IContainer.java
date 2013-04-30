/**
 * 
 */
package stream.runtime;

import java.util.List;
import java.util.Set;

import stream.ComputeGraph;
import stream.Process;
import stream.io.Source;
import stream.runtime.setup.ServiceReference;

/**
 * @author chris
 *
 */
public interface IContainer {

	public abstract ComputeGraph computeGraph();

	public abstract Set<Source> getStreams();

	/**
	 * @return the name
	 */
	public abstract String getName();

	public abstract ContainerContext getContext();

	/**
	 * @return the processes
	 */
	public abstract List<Process> getProcesses();

	/**
	 * @return the serviceRefs
	 */
	public abstract List<ServiceReference> getServiceRefs();

	public abstract Variables getVariables();

}