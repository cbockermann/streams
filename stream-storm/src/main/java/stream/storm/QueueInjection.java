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
import stream.runtime.setup.ParameterInjection;
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
		return;
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
			log.info("Checking method {}", m);
			if (ParameterInjection.isQueueSetter(m)) {
				final String qsn = getQueueSetterName(m);
				String prop = qsn.substring(0, 1).toLowerCase() + qsn.substring(1);
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
					Object array = wrapper.toArray(new QueueWrapper[wrapper.size()]);
					m.invoke(p, array);

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