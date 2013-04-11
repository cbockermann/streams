/**
 * 
 */
package stream.storm;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import stream.Processor;
import stream.ProcessorList;
import stream.io.Sink;
import stream.runtime.setup.ObjectFactory;
import stream.runtime.setup.ProcessorFactory.ProcessorCreationHandler;
import backtype.storm.task.OutputCollector;

/**
 * @author chris
 * 
 */
public class QueueInjection implements ProcessorCreationHandler {

	static Logger log = LoggerFactory.getLogger(QueueInjection.class);

	final OutputCollector collector;

	public QueueInjection(OutputCollector c) {
		this.collector = c;
	}

	public static void injectQueues(Processor proc, OutputCollector collector) {
	}

	public static void injectQueues(ProcessorList procs,
			OutputCollector collector) {
		for (Processor p : procs.getProcessors()) {
			injectQueues(p, collector);
		}
	}

	public static String getQueueSetterName(Method m) {
		return m.getName().substring(3);
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
		if (type.isArray()) {
			if (Sink.class.isAssignableFrom(type)) {
				log.info("Found setter for type '{}': {}", Sink.class, m);
				return true;
			}

		} else {

			Class<?> ct = type.getComponentType();
			if (Sink.class.isAssignableFrom(ct)) {
				log.info("Found setter for array-type '{}': {}", Sink.class, m);
				return true;
			}
		}

		return true;
	}

	public static boolean isQueueArraySetter(Method m) {
		Class<?> type = m.getParameterTypes()[0];
		return type.isArray();
	}

	/**
	 * @see stream.runtime.setup.ProcessorFactory.ProcessorCreationHandler#processorCreated(stream.Processor,
	 *      org.w3c.dom.Element)
	 */
	@Override
	public void processorCreated(Processor p, Element from) throws Exception {
		Map<String, String> params = ObjectFactory.newInstance().getAttributes(
				from);
		for (Method m : p.getClass().getMethods()) {

			if (isQueueSetter(m)) {
				String prop = getQueueSetterName(m);
				log.info("Found queue-setter for property {}", prop);

				if (isQueueArraySetter(m)) {
					String[] names = params.get(prop).split(",");

					List<QueueWrapper> wrapper = new ArrayList<QueueWrapper>();
					for (String name : names) {
						if (!name.trim().isEmpty()) {
							wrapper.add(new QueueWrapper(collector, name));
						}
					}
					log.info("Injecting array of queues...");
					m.invoke(p, wrapper.toArray());

				} else {
					String name = params.get(prop);
					log.info("Injecting a single queue...");
					m.invoke(p, new QueueWrapper(collector, name));
				}
			} else {
				log.info("Skipping method {} => not a queue-setter", m);
			}
		}
	}
}