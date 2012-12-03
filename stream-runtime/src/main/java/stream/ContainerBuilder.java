/**
 * 
 */
package stream;

import java.io.File;

import org.w3c.dom.Document;

import stream.runtime.Container;
import stream.service.NamingService;
import stream.util.XMLUtils;

/**
 * @author chris
 * 
 */
public class ContainerBuilder {

	/**
	 * How to build a container...
	 * 
	 * @param xml
	 * @return
	 */
	public Container build(File xml) throws Exception {

		Document xmlDoc = XMLUtils.parseDocument(xml);

		NamingService ns = null;

		//
		//
		ComputeGraph graph = createComputeGraph(xmlDoc);

		// RTG = Streams-Wrapper Graph fuer Process,Stream,... || Storm
		// Topology?
		//
		RuntimeGraph rtg = createRuntimeGraph(graph);

		LifeCycleGraph lfg = createLifeCycleGraph(rtg);

		//
		//
		Container container = new ContainerImpl(lfg, ns);

		registerServices(graph, ns);

		return container;
	}

	/**
	 * @param graph
	 * @return
	 */
	private RuntimeGraph createRuntimeGraph(ComputeGraph graph) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param graph
	 * @param ns
	 */
	private void registerServices(ComputeGraph graph, NamingService ns) {
		// TODO Auto-generated method stub

	}

	/**
	 * @param graph
	 * @return
	 */
	private LifeCycleGraph createLifeCycleGraph(RuntimeGraph graph) {
		// TODO Auto-generated method stub
		return null;
	}

	protected ComputeGraph createComputeGraph(Document xml) {
		return null;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	//
	// don't look below this line
	//

	public class ContainerImpl implements Container {
		protected final LifeCycleGraph lcg;
		protected final NamingService ns;

		public ContainerImpl(LifeCycleGraph lcg, NamingService ns) {
			this.lcg = lcg;
			this.ns = ns;
		}
	}

	public interface ComputeGraph {

	}
}
