/**
 * 
 */
package stream.container;

import java.util.List;
import java.util.Set;

import stream.Context;
import stream.Process;
import stream.app.ComputeGraph;
import stream.io.Source;
import stream.runtime.ServiceReference;
import stream.service.NamingService;
import stream.util.Variables;

/**
 * @author chris, Hendrik Blom
 *
 */
public interface IContainer {

	public abstract ComputeGraph computeGraph();

	public abstract Set<Source> getStreams();

	/**
	 * @return the name
	 */
	public abstract String getName();

	public abstract Context getContext();

	/**
	 * @return the processes
	 */
	public abstract List<Process> getProcesses();

	/**
	 * @return the serviceRefs
	 */
	public abstract List<ServiceReference> getServiceRefs();

	public abstract Variables getVariables();
	
	public abstract NamingService getNamingService();

}