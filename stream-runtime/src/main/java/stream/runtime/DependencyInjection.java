/**
 * 
 */
package stream.runtime;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.ComputeGraph;
import stream.ComputeGraph.ServiceRef;
import stream.ComputeGraph.SinkRef;
import stream.ComputeGraph.SourceRef;
import stream.Reference;
import stream.io.Queue;
import stream.io.Sink;
import stream.io.Source;
import stream.runtime.setup.ParameterInjection;
import stream.service.NamingService;
import stream.service.Service;

/**
 * 
 * 
 * @author Christian Bockermann
 * 
 */
public class DependencyInjection {

	static Logger log = LoggerFactory.getLogger(DependencyInjection.class);

	final List<Reference> refs = new ArrayList<Reference>();

	public void add(Reference ref) {
		refs.add(ref);
	}

	public void addAll(Collection<Reference> refs) {
		this.refs.addAll(refs);
	}

	public void injectDependencies(ComputeGraph graph,
			NamingService namingService) throws Exception {

		log.debug("Found {} references to be resolved...", refs);

		Iterator<Reference> it = refs.iterator();
		while (it.hasNext()) {
			Reference ref = it.next();
			log.debug("next unresolved reference is {}", ref);
			boolean success = inject(ref, graph, namingService);
			if (success) {
				log.debug("Successfully injected dependency {}", ref);
				it.remove();
			} else {
				log.error("Failed to resolve dependency {}", ref);
			}
		}

		if (!refs.isEmpty()) {
			throw new Exception(refs.size() + " unresolved dependencies!");
		}
	}

	private boolean inject(Reference ref, ComputeGraph graph,
			NamingService namingService) throws Exception {

		if (ref instanceof SinkRef)
			return inject((SinkRef) ref, graph);

		if (ref instanceof SourceRef)
			return inject((SourceRef) ref, graph);

		if (ref instanceof ServiceRef)
			return inject((ServiceRef) ref, graph, namingService);

		return false;
	}

	private boolean inject(SinkRef ref, ComputeGraph graph) throws Exception {
		log.debug("Injecting sink reference {}", ref);
		String[] refs = ref.ids();
		Sink[] sinks = new Sink[refs.length];
		for (int i = 0; i < sinks.length; i++) {
			sinks[i] = graph.sinks().get(refs[i]);
			if (sinks[i] == null) {
				Queue queue = new stream.io.BlockingQueue();
				graph.addQueue(refs[i], queue);

				if (queue instanceof Service) {
					graph.addService(refs[i], (Service) queue);
				}
				log.debug("Creating implicitly defined queue: {}", queue);
				sinks[i] = queue;
			}
		}
		return injectResolvedReferences(ref.object(), ref.property(), sinks);
	}

	private boolean inject(SourceRef ref, ComputeGraph graph) throws Exception {
		log.debug("Injecting source reference {}", ref);
		String[] refs = ref.ids();
		Source[] sources = new Source[refs.length];
		for (int i = 0; i < sources.length; i++) {
			sources[i] = graph.sources().get(refs[i]);
			// TODO Create Queue
			if (sources[i] == null) {
				Queue queue = new stream.io.BlockingQueue();
				graph.addQueue(refs[i], queue);

				if (queue instanceof Service) {
					graph.addService(refs[i], (Service) queue);
				}
				log.info("Created new Queue:{} {}", queue.getId(), queue);
				sources[i] = queue;
			}
		}

		return injectResolvedReferences(ref.object(), ref.property(), sources);
	}

	private boolean inject(ServiceRef ref, ComputeGraph graph,
			NamingService namingService) throws Exception {
		log.debug("Injecting service reference {}", ref);

		String[] refs = ref.ids();
		Service[] services = new Service[refs.length];
		for (int i = 0; i < services.length; i++) {
			services[i] = namingService.lookup(refs[i], ref.type());
		}

		return injectResolvedReferences(ref.object(), ref.property(), services);
	}

	public boolean injectResolvedReferences(Object o, String property,
			Object[] resolvedRefs) throws Exception {
		String name = "set" + property.toLowerCase();

		for (Method m : o.getClass().getMethods()) {
			if (m.getName().toLowerCase().equalsIgnoreCase(name)
					&& m.getParameterTypes().length == 1) {

				Class<?> type = m.getParameterTypes()[0];
				if (type.isArray()) {

					Object values = Array.newInstance(type.getComponentType(),
							resolvedRefs.length);
					for (int i = 0; i < Array.getLength(values); i++) {
						Array.set(values, i, (resolvedRefs[i]));
					}
					log.debug("Injecting   '{}'.{}   <-- " + values, o,
							property);
					log.debug("Calling method  '{}'", m);
					// try {
					m.invoke(o, values);
					// } catch (IllegalArgumentException e) {
					// throw new IllegalArgumentException(
					// "IllegalArgumentException" + o + ":"
					// + values.toString());
					//
					// }
				} else {
					log.debug("Injecting   '{}'.{}   <-- " + resolvedRefs[0],
							o, property);
					log.debug("Calling method  '{}' with arg '{}'", m,
							resolvedRefs[0]);
					// try {
					m.invoke(o, new Object[] { resolvedRefs[0] });
					// } catch (IllegalArgumentException e) {
					// throw new IllegalArgumentException(
					// "IllegalArgumentException" + o + ":"
					// + resolvedRefs[0]);
					// }
				}
				return true;
			}
		}

		return false;
	}

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

	@SuppressWarnings("unchecked")
	public static Class<? extends Service> hasServiceSetter(String name,
			Object o) {
		try {

			for (Method m : o.getClass().getMethods()) {
				if (m.getName().equalsIgnoreCase("set" + name)
						&& isServiceSetter(m)) {
					return (Class<? extends Service>) m.getParameterTypes()[0];
				}
			}

			return null;
		} catch (Exception e) {
			log.error("Failed to determine service-setter: {}", e.getMessage());
			return null;
		}
	}

	/**
	 * This method checks whether the provided method is a service setter, i.e.
	 * it is a setter method to inject service references into the object.
	 * 
	 * This requires the method to provide the following characteristics:
	 * <ol>
	 * <li>Its names starts with <code>set</code> and provides additional
	 * characters, i.e. <code>set</code> alone is not enough.</li>
	 * <li>It takes a single parameter, which is a service implementation</li>
	 * </ol>
	 * 
	 * @param m
	 * @return
	 */
	public static boolean isServiceSetter(Method m) {

		if (!m.getName().startsWith("set"))
			return false;

		Class<?>[] paramTypes = m.getParameterTypes();
		if (paramTypes.length != 1)
			return false;

		return isServiceImplementation(paramTypes[0]);
	}

	public static boolean isSourceSetter(Method m) {
		if (!m.getName().startsWith("set"))
			return false;

		Class<?>[] paramTypes = m.getParameterTypes();
		if (paramTypes.length != 1)
			return false;

		return Source.class.isAssignableFrom(paramTypes[0]);
	}

	public static boolean isSinkSetter(Method m) {
		return isSetter(m, Sink.class);
	}

	public static boolean isSinkArraySetter(Method m) {
		return isArraySetter(m, Sink.class);
	}

	public static boolean isSetter(Method m, Class<?> type) {
		if (!m.getName().startsWith("set")) {
			return false;
		}

		Class<?>[] paramTypes = m.getParameterTypes();
		if (paramTypes.length != 1) {
			return false;
		}

		if (paramTypes[0].isArray()) {
			return type.isAssignableFrom(paramTypes[0].getComponentType());
		} else {
			return type.isAssignableFrom(paramTypes[0]);
		}
	}

	public static boolean isArraySetter(Method m, Class<?> type) {
		if (isSetter(m, type)) {
			return m.getParameterTypes()[0].isArray();
		}
		return false;
	}

	/**
	 * This method checks whether the given class implements the Service
	 * interface.
	 * 
	 * @param clazz
	 * @return
	 */
	public static boolean isServiceImplementation(Class<?> clazz) {

		if (clazz == Service.class)
			return true;

		if (clazz.isArray()) {
			log.debug("checking array component-type for service implementation");
			return isServiceImplementation(clazz.getComponentType());
			// log.debug("Injection of arrays of service references is not yet supported!");
			// return false;
		}

		// TODO: Is 'isAssignableFrom(..)' the better way here?
		//
		if (Service.class.isAssignableFrom(clazz))
			return true;

		for (Class<?> intf : clazz.getInterfaces()) {
			log.trace("Checking if {} = {}", intf, Service.class);
			if (intf.equals(Service.class) || intf == Service.class) {
				log.trace("Yes, class {} implements the service interface!",
						clazz);
				return true;
			}
		}

		log.trace("No, class {} does not implement the service interface!",
				clazz);
		return false;
	}
}