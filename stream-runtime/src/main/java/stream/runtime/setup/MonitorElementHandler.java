/**
 * 
 */
package stream.runtime.setup;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import stream.Processor;
import stream.runtime.Monitor;
import stream.runtime.ProcessContainer;

/**
 * @author chris
 * 
 */
public class MonitorElementHandler extends ProcessElementHandler {

	static Logger log = LoggerFactory.getLogger(MonitorElementHandler.class);

	/**
	 * @param objectFactory
	 * @param processorFactory
	 */
	public MonitorElementHandler(ObjectFactory objectFactory,
			ProcessorFactory processorFactory) {
		super(objectFactory, processorFactory);
	}

	/**
	 * @see stream.runtime.setup.ElementHandler#getKey()
	 */
	@Override
	public String getKey() {
		return "Monitor";
	}

	/**
	 * @see stream.runtime.setup.ElementHandler#handlesElement(org.w3c.dom
	 *      .Element)
	 */
	@Override
	public boolean handlesElement(Element element) {
		return "monitor".equalsIgnoreCase(element.getNodeName());
	}

	/**
	 * @see stream.runtime.setup.ElementHandler#handleElement(stream.runtime
	 *      .ProcessContainer, org.w3c.dom.Element)
	 */
	@Override
	public void handleElement(ProcessContainer container, Element element)
			throws Exception {
		Map<String, String> params = objectFactory.getAttributes(element);

		// the default Monitor class is stream.runtime.Monitor
		//
		String className = "stream.runtime.Monitor";
		if (element.hasAttribute("class")) {
			className = element.getAttribute("class");
			log.info("Creating Monitor instance from custom class '{}'",
					className);
		}

		Monitor monitor = (Monitor) objectFactory.create(className, params);
		log.debug("Created Monitor object: {}", monitor);

		List<Processor> procs = createNestedProcessors(container, element);
		for (Processor p : procs)
			monitor.addProcessor(p);

		container.getProcesses().add(monitor);
	}
}
