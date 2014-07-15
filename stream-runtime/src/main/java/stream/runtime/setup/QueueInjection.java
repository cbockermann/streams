/**
 * 
 */
package stream.runtime.setup;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.app.ComputeGraph;
import stream.app.ComputeGraph.SinkRef;
import stream.io.Queue;
import stream.io.Sink;
import stream.runtime.ProcessContainer;

/**
 * <p>
 * This class implements the injection of queues, sinks and sources into the
 * prepared compute graph.
 * </p>
 * 
 * 
 * @author Christian Bockermann
 * @deprecated Will be replaced by the new DependencyInjection class
 */
public class QueueInjection {

	static Logger log = LoggerFactory.getLogger(QueueInjection.class);

	@SuppressWarnings("unchecked")
	public static Class<? extends Sink> hasSinkSetter(String name, Object o) {

		for (Method m : o.getClass().getMethods()) {

			if (!m.getName().toLowerCase().equals("set" + name))
				continue;

			if (ParameterInjection.isQueueSetter(m)) {
				return (Class<? extends Sink>) m.getParameterTypes()[0];
			}

		}

		return null;
	}

	public static boolean isQueueSetter(Method m) {

		if (!m.getName().toLowerCase().startsWith("set")) {
			log.debug("Not a setter -> method not starting with 'set'");
			return false;
		}

		Class<?>[] types = m.getParameterTypes();
		if (types.length != 1) {
			log.debug("Not a setter, parameter types: {}", (Object[]) types);
			return false;
		}

		Class<?> type = types[0];
		if (!type.isArray()) {
			if (Sink.class.isAssignableFrom(type)) {
				log.debug("Found setter for type '{}': {}", Sink.class, m);
				return true;
			}

		} else {

			Class<?> ct = type.getComponentType();
			if (ct != null && Sink.class.isAssignableFrom(ct)) {
				log.debug("Found setter for array-type '{}': {}", Sink.class, m);
				return true;
			}
		}

		return false;
	}

	public static boolean isQueueArraySetter(Method m) {
		Class<?> type = m.getParameterTypes()[0];
		if (!type.isArray())
			return false;

		Class<?> comp = type.getComponentType();
		return isQueue(comp);
	}

	public final static boolean isSink(Class<?> type) {
		return Sink.class.isAssignableFrom(type);
	}

	public final static boolean isQueue(Class<?> type) {
		return Queue.class.isAssignableFrom(type);
	}

	/**
	 * 
	 * 
	 * @param graph
	 * @param pc
	 * @throws Exception
	 */
	public static void injectQueues(final ComputeGraph graph,
			final ProcessContainer pc) throws Exception {

		for (SinkRef sref : graph.sinkRefs()) {

			String[] refs = sref.ids();
			Sink[] sinks = new Sink[refs.length];

			for (int i = 0; i < refs.length; i++) {
				sinks[i] = graph.sinks().get(refs[i]);
			}

			log.info("Injecting queues '{}' into object {}", sinks,
					sref.object());
			injectSink(sref.object(), sref.property(), sinks);
		}
	}

	protected static void injectSink(Object o, String property, Sink[] sinks) {
		throw new UnsupportedOperationException("Not yet implemented!");
	}
}