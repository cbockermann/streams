/**
 * 
 */
package stream.runtime.setup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import stream.Processor;
import stream.ProcessorList;
import stream.runtime.ElementHandler;
import stream.runtime.Process;
import stream.runtime.ProcessContainer;
import stream.service.Service;

/**
 * @author chris
 * 
 */
public class ProcessElementHandler implements ElementHandler {

	static Logger log = LoggerFactory.getLogger(ProcessElementHandler.class);
	protected final ObjectFactory objectFactory;
	protected final ProcessorFactory processorFactory;

	public ProcessElementHandler(ObjectFactory objectFactory,
			ProcessorFactory processorFactory) {
		this.objectFactory = objectFactory;
		this.processorFactory = processorFactory;
	}

	/**
	 * @see stream.runtime.ElementHandler#getKey()
	 */
	@Override
	public String getKey() {
		return "Process";
	}

	/**
	 * @see stream.runtime.ElementHandler#handlesElement(org.w3c.dom.Element)
	 */
	@Override
	public boolean handlesElement(Element element) {
		return "process".equalsIgnoreCase(element.getNodeName());
	}

	/**
	 * @see stream.runtime.ElementHandler#handleElement(stream.runtime.ProcessContainer
	 *      , org.w3c.dom.Element)
	 */
	@Override
	public void handleElement(ProcessContainer container, Element element)
			throws Exception {

		Map<String, String> attr = objectFactory.getAttributes(element);
		String src = attr.get("source");
		if (src == null)
			src = attr.get("input");

		// Create the default data-stream process
		//
		String processClass = "stream.runtime.Process";
		if (attr.containsKey("class")) {
			processClass = attr.get("class");
			log.info("Using custom process class '{}'", processClass);
		}

		String id = attr.get("id");
		if (id == null || "".equals(id.trim())) {
			id = "process";
		}

		String multi = attr.get("multiply");
		if (multi != null && !"".equals(multi.trim())) {

			Integer times = new Integer(multi);

			for (int i = 0; i < times; i++) {
				String pid = id + ":" + i;
				objectFactory.set("process.id", pid);
				log.info("Creating process '{}'", pid);
				Process process = createProcess(processClass, attr, container,
						element);
				container.getProcesses().add(process);
			}

		} else {
			objectFactory.set("process.id", id);
			Process process = createProcess(processClass, attr, container,
					element);
			log.debug("Created Process object: {}", process);
			container.getProcesses().add(process);
		}
	}

	protected Process createProcess(String processClass,
			Map<String, String> attr, ProcessContainer container,
			Element element) throws Exception {
		Process process = (Process) objectFactory.create(processClass, attr);
		log.debug("Created Process object: {}", process);

		List<Processor> procs = createNestedProcessors(container, element);
		for (Processor p : procs) {
			process.addProcessor(p);
		}
		return process;
	}

	protected Processor createProcessor(ProcessContainer container,
			Element child) throws Exception {

		Map<String, String> params = objectFactory.getAttributes(child);

		Object o = objectFactory.create(child);
		if (o instanceof Processor) {

			if (o instanceof ProcessorList) {

				NodeList children = child.getChildNodes();
				for (int i = 0; i < children.getLength(); i++) {

					Node node = children.item(i);
					if (node.getNodeType() == Node.ELEMENT_NODE) {

						Element element = (Element) node;
						Processor proc = createProcessor(container, element);
						if (proc != null) {
							((ProcessorList) o).addProcessor(proc);
						} else {
							log.warn(
									"Nested element {} is not of type 'stream.data.Processor': ",
									node.getNodeName());
						}
					}
				}
			}

			if (params.containsKey("id") && !"".equals(params.get("id").trim())) {

				if (o instanceof Service) {
					String id = params.get("id").trim();
					log.debug(
							"Registiering processor with id '{}' in look-up service",
							child.getAttribute("id"));
					container.getContext().register(id, (Service) o);

				} else {
					log.warn(
							"Processor '{}' specifies an ID attribute '{}' but does not implement a Service interface. Processor will *not* be registered!",
							o.getClass().getName(), params.get("id"));
				}
			}

			for (String key : params.keySet()) {

				if (key.endsWith("-ref")) {
					String ref = params.get(key);
					ServiceReference serviceRef = new ServiceReference(ref, o,
							key);
					container.getServiceRefs().add(serviceRef);
				}
			}

			return (Processor) o;
		}

		return null;
	}

	protected List<Processor> createNestedProcessors(
			ProcessContainer container, Element child) throws Exception {
		List<Processor> procs = new ArrayList<Processor>();

		NodeList pnodes = child.getChildNodes();
		for (int j = 0; j < pnodes.getLength(); j++) {

			Node cnode = pnodes.item(j);
			if (cnode.getNodeType() == Node.ELEMENT_NODE) {
				Processor p = createProcessor(container, (Element) cnode);
				if (p != null) {
					log.debug("Found processor...");
					procs.add(p);
				}
			}
		}
		return procs;
	}

}
