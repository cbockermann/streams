/**
 * 
 */
package stream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.io.Sink;
import stream.io.Source;
import stream.io.Stream;
import stream.runtime.AbstractProcess;
import stream.runtime.LifeCycle;
import stream.service.Service;

/**
 * <p>
 * The compute graph is the class that provides book keeping about the data flow
 * of a process container. Essentially, the compute graph is used in three
 * different stages:
 * 
 * <ol>
 * <li>During parsing of the configuration, the graph is filled up with nodes
 * such as streams, processes, queues or services.</li>
 * <li>When preparing the startup of the container, the book-keeping information
 * is used for injecting the streams, queues and services.</li>
 * <li>While the container is running, the compute graph is used to check when
 * the container is finished.</li>
 * </ol>
 * </p>
 * 
 * @author Christian Bockermann
 * 
 */
public class ComputeGraph {

	static Logger log = LoggerFactory.getLogger(ComputeGraph.class);

	final Set<Object> nodes = new LinkedHashSet<Object>();
	final List<Edge> edges = new ArrayList<Edge>();

	/** The list of references from elements to queues */
	final List<SinkRef> queueRefs = new ArrayList<SinkRef>();

	final List<SourceRef> sourceRefs = new ArrayList<SourceRef>();

	/** The list of references from elements to services */
	final List<ServiceRef> serviceRefs = new ArrayList<ServiceRef>();

	/** The services defined in the graph */
	final Map<String, Service> services = new LinkedHashMap<String, Service>();

	/** The streams defined in the graph */
	final Map<String, Source> sources = new LinkedHashMap<String, Source>();

	/** The queues defined in the graph */
	final Map<String, Sink> sinks = new LinkedHashMap<String, Sink>();

	/** The process nodes of the graph */
	final Map<String, Process> processes = new LinkedHashMap<String, Process>();

	public synchronized void add(Object from, Object to) {
		add(from);
		add(to);
		edges.add(new Edge(from, to));
		this.notify();
	}

	public void add(Object node) {
		nodes.add(node);
	}

	/**
	 * This method returns a set of sources that are referenced as inputs (e.g.
	 * by processes) but are not referenced as outputs (e.g. outputs by
	 * "enqueue").
	 * 
	 * @return
	 */
	public synchronized Set<Object> getRootSources() {
		Set<Object> sources = getSources();
		Set<Object> srcs = new LinkedHashSet<Object>();
		Iterator<Object> it = sources.iterator();
		while (it.hasNext()) {
			Object source = it.next();
			if (source instanceof Source) {
				if (!getSourcesFor(source).isEmpty()) {
					it.remove();
				}
			}
			srcs.add(source);
		}
		return srcs;
	}

	public synchronized Set<Object> getSources() {
		Set<Object> nodes = new LinkedHashSet<Object>();
		for (Edge edge : edges) {
			nodes.add(edge.getFrom());
		}
		return nodes;
	}

	public synchronized Set<Object> getTargets(Object from) {
		Set<Object> nodes = new LinkedHashSet<Object>();
		for (Edge edge : edges) {
			if (edge.getFrom() == from)
				nodes.add(edge.getTo());
		}
		return nodes;
	}

	public synchronized Set<Object> getReferencedObjects() {
		Set<Object> nodes = new LinkedHashSet<Object>();
		for (Edge edge : edges) {
			nodes.add(edge.getTo());
		}
		return nodes;
	}

	public synchronized Set<Object> getSourcesFor(Object target) {
		Set<Object> nodes = new LinkedHashSet<Object>();
		for (Edge edge : edges) {
			if (edge.getTo() == target)
				nodes.add(edge.getFrom());
		}
		return nodes;
	}

	public synchronized Set<Object> getIsolated() {
		Set<Object> nodes = new LinkedHashSet<Object>();
		for (Object node : this.nodes) {
			if (getSourcesFor(node).isEmpty())
				nodes.add(node);
		}
		return nodes;
	}

	public Set<Object> nodes() {
		return Collections.unmodifiableSet(nodes);
	}

	public synchronized void clear() {
		nodes.clear();
		edges.clear();
		this.notify();
	}

	public synchronized List<LifeCycle> remove(Object o) {
		if (!nodes.contains(o)) {
			return new ArrayList<LifeCycle>();
		}

		List<LifeCycle> objs = remove(o, false);
		this.notifyAll();
		return objs;
	}

	private synchronized List<LifeCycle> remove(Object o, boolean notify) {
		log.debug("Removing {} from dependency-graph...", o);
		List<LifeCycle> lifeObjects = new ArrayList<LifeCycle>();
		if (!nodes.contains(o)) {
			return lifeObjects;
		}

		if (o instanceof LifeCycle) {
			lifeObjects.add((LifeCycle) o);
		}

		if (o instanceof Source) {
			try {
				log.info("Removing and closing source {}", o);
				synchronized (o) {
					((Source) o).close();
				}
			} catch (Exception e) {
				log.error("Failed to close source '{}': ", o, e.getMessage());
				if (log.isDebugEnabled())
					e.printStackTrace();
			}
		}

		// is this at all required? it does not hurt, though, but the
		// compute-graph should in theory only consist of sinks, sources and
		// processes.
		// CB: Yes, it IS required as is removes the edges of processors to
		// queues.
		//
		if (o instanceof AbstractProcess) {
			List<Processor> processors = ((AbstractProcess) o).getProcessors();
			log.debug("Removing {} nested processors of {}", processors.size(),
					o);
			for (Processor p : processors) {
				remove(p, notify);
			}
		}

		Iterator<Edge> it = (new ArrayList<Edge>(edges)).iterator();
		while (it.hasNext()) {
			Edge edge = it.next();

			if (edge.getFrom() == o) {
				log.debug("[graph-shutdown]   Removing edge ( {} => {} )",
						edge.getFrom(), edge.getTo());
				this.nodes.remove(o);
				this.edges.remove(edge);
				Object target = edge.getTo();
				if (this.getSourcesFor(target).isEmpty()) {
					log.debug(
							"[graph-shutdown]     -> No more references to {}, adding to shutdown-queue",
							target);
					lifeObjects.addAll(remove(target, notify));
				} else {
					log.debug("target {} has {} references left", target,
							getSourcesFor(target).size());
				}
			}

			if (edge.getTo() == o) {

			}
		}

		log.debug("[dep-graph]  Reference counts: ");
		for (Object node : this.nodes) {
			log.debug("[dep-graph]     * {}  is referenced by {} ", node,
					this.getSourcesFor(node));
		}
		return lifeObjects;
	}

	public synchronized void printShutdownStrategy() {

		List<Object> all = new ArrayList<Object>();
		all.addAll(this.nodes);

		Set<Object> finished = new LinkedHashSet<Object>();
		Queue<Object> waiting = new LinkedBlockingQueue<Object>();
		waiting.addAll(getIsolated());

		while (!waiting.isEmpty()) {
			Object next = waiting.poll();
			log.trace("[graph-shutdown]   Shutting down {}", next);
			finished.add(next);
			all.remove(next);
		}
		log.trace("[dep-graph]  Reference counts: ");
		for (Object node : this.nodes) {
			log.trace("[dep-graph]     * {}  is referenced by {} objects",
					node, this.getSourcesFor(node).size());
		}
	}

	public Collection<Object> getAll(Class<?> pattern) {
		List<Object> matching = new ArrayList<Object>();
		for (Object o : this.nodes) {
			if (pattern.isAssignableFrom(o.getClass())) {
				matching.add(o);
			}
		}
		return matching;
	}

	public void addReference(SinkRef qref) {
		this.queueRefs.add(qref);
	}

	public void addReference(SourceRef ref) {
		this.sourceRefs.add(ref);
	}

	public List<SourceRef> sourceRefs() {
		return sourceRefs;
	}

	public List<SinkRef> sinkRefs() {
		return queueRefs;
	}

	public void addReference(ServiceRef sref) {
		this.serviceRefs.add(sref);
	}

	public List<ServiceRef> serviceRefs() {
		return serviceRefs;
	}

	/**
	 * Adds a service to the compute graph.
	 * 
	 * @param id
	 *            The id of the service.
	 * @param service
	 *            The instance of the service.
	 */
	public void addService(String id, Service service) {
		if (services.containsKey(id))
			throw new RuntimeException("A service with id '" + id
					+ "' has already been defined!");
		this.services.put(id, service);
	}

	public Map<String, Service> services() {
		return Collections.unmodifiableMap(services);
	}

	/**
	 * Adds a stream with the given ID to the compute graph.
	 * 
	 * @param id
	 *            The id of the stream.
	 * @param stream
	 *            The instance of the stream.
	 */
	public void addStream(String id, Stream stream) {
		if (sources.containsKey(id))
			throw new RuntimeException("A stream with id '" + id
					+ "' has already been defined!");
		this.sources.put(id, stream);
	}

	public Map<String, Source> sources() {
		return Collections.unmodifiableMap(sources);
	}

	/**
	 * Adds a process with the given ID to the compute graph.
	 * 
	 * @param id
	 *            The id of the process.
	 * @param process
	 *            The instance of the process.
	 */
	public void addProcess(String id, Process process) {
		if (processes.containsKey(id))
			throw new RuntimeException("A process with id '" + id
					+ "' has already been defined!");
		this.processes.put(id, process);

		// We add a reference from the process to each processor. Later these
		// references are used to remove the processors from the runtime graph
		// as soon as the processes is being terminated.
		//
		for (Processor p : process.getProcessors()) {
			this.add(process, p);
		}
	}

	public Map<String, Process> processes() {
		return Collections.unmodifiableMap(processes);
	}

	public void addQueue(String id, stream.io.Queue queue) {
		if (sinks.containsKey(id)) {
			throw new RuntimeException("A queue with id '" + id
					+ "' has already been defined!");
		}

		if (sources.containsKey(id)) {
			throw new RuntimeException("A stream with id '" + id
					+ "' has already been defined!");
		}

		sinks.put(id, queue);
		sources.put(id, queue);
	}

	public Map<String, Sink> sinks() {
		return Collections.unmodifiableMap(sinks);
	}

	public static class Edge {
		static Integer lastId = 0;
		final Integer id = lastId++;
		final Object from;
		final Object to;

		public Edge(Object from, Object to) {
			this.from = from;
			this.to = to;
		}

		public Object getFrom() {
			return from;
		}

		public Object getTo() {
			return to;
		}
	}

	public final static class SinkRef extends Reference {

		public SinkRef(Object o, String property, String queueId) {
			super(o, property, queueId);
		}

		public SinkRef(Object o, String property, String[] refs) {
			super(o, property, refs);
		}
	}

	public final static class SourceRef extends Reference {

		public SourceRef(Object o, String property, String queueId) {
			super(o, property, queueId);
		}
	}

	public final static class ServiceRef extends Reference {
		final Class<? extends Service> type;

		public ServiceRef(Object o, String property, String queueId,
				Class<? extends Service> type) {
			super(o, property, queueId);
			this.type = type;
		}

		public ServiceRef(Object o, String property, String[] queueId,
				Class<? extends Service> type) {
			super(o, property, queueId);
			this.type = type;
		}

		public Class<? extends Service> type() {
			return type;
		}
	}
}