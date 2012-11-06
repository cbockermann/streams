/**
 * 
 */
package stream.runtime.shutdown;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.runtime.LifeCycle;

/**
 * @author chris
 * 
 */
public class DependencyGraph {

	static Logger log = LoggerFactory.getLogger(DependencyGraph.class);
	final Set<Object> nodes = new LinkedHashSet<Object>();
	final List<Edge> edges = new ArrayList<Edge>();

	public synchronized void add(Object from, Object to) {
		nodes.add(from);
		nodes.add(to);
		edges.add(new Edge(from, to));
		this.notify();
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
		this.notify();
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
		Iterator<Edge> it = (new ArrayList<Edge>(edges)).iterator();
		while (it.hasNext()) {
			Edge edge = it.next();
			if (edge.getFrom() == o) {
				// log.debug("[graph-shutdown]   Removing edge ( {} => {} )",
				// edge.getFrom(),
				// edge.getTo());
				this.nodes.remove(o);
				this.edges.remove(edge);
				Object target = edge.getTo();
				if (this.getSourcesFor(target).isEmpty()) {
					log.trace(
							"[graph-shutdown]     -> No more references to {}, adding to shutdown-queue",
							target);
					lifeObjects.addAll(remove(target, notify));
				}
			}
		}

		log.trace("[dep-graph]  Reference counts: ");
		for (Object node : this.nodes) {
			log.trace("[dep-graph]     * {}  is referenced by {} ", node,
					this.getSourcesFor(node));
		}
		return lifeObjects;
	}

	public void printShutdownStrategy() {
		Set<Object> finished = new LinkedHashSet<Object>();
		Queue<Object> waiting = new LinkedBlockingQueue<Object>();
		waiting.addAll(getIsolated());

		while (!waiting.isEmpty()) {
			Object next = waiting.poll();
			log.trace("[graph-shutdown]   Shutting down {}", next);
			finished.add(next);
			remove(next);
		}
		log.trace("[dep-graph]  Reference counts: ");
		for (Object node : this.nodes) {
			log.trace("[dep-graph]     * {}  is referenced by {} objects",
					node, this.getSourcesFor(node).size());
		}
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
}