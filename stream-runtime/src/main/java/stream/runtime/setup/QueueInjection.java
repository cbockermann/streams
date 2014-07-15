/*
 *  streams library
 *
 *  Copyright (C) 2011-2014 by Christian Bockermann, Hendrik Blom
 * 
 *  streams is a library, API and runtime environment for processing high
 *  volume data streams. It is composed of three submodules "stream-api",
 *  "stream-core" and "stream-runtime".
 *
 *  The streams library (and its submodules) is free software: you can 
 *  redistribute it and/or modify it under the terms of the 
 *  GNU Affero General Public License as published by the Free Software 
 *  Foundation, either version 3 of the License, or (at your option) any 
 *  later version.
 *
 *  The stream.ai library (and its submodules) is distributed in the hope
 *  that it will be useful, but WITHOUT ANY WARRANTY; without even the implied 
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/.
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