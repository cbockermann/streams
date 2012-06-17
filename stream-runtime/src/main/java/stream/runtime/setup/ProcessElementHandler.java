/*
 *  streams library
 *
 *  Copyright (C) 2011-2012 by Christian Bockermann, Hendrik Blom
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

import java.util.ArrayList;
import java.util.HashMap;
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
import stream.runtime.VariableContext;
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

		String copies = attr.get("copies");
		if (attr.containsKey("multiply")) {
			copies = attr.get("multiply");
			log.warn("The attribute 'multiply' is deprecated for element 'Process'");
			log.warn("Please use 'copies' instead of 'multiply'.");
		}

		if (copies != null && !"".equals(copies.trim())) {

			VariableContext var = new VariableContext(container.getContext()
					.getProperties());
			copies = var.expand(copies);

			Integer times = new Integer(copies);

			for (int i = 0; i < times; i++) {
				String pid = "" + i;
				objectFactory.set("process.id", pid);
				objectFactory.set("copy.id", pid);
				Map<String, String> extra = new HashMap<String, String>();
				extra.put("process.id", pid);
				extra.put("copy.id", pid);
				var.addVariables(extra);
				log.info("Creating process '{}'", pid);
				Process process = createProcess(processClass, attr, container,
						element, extra);

				String input = var.expand(src);
				log.info("Setting source for process {} to {}", process, input);
				process.setInput(input);
				container.getProcesses().add(process);
			}

		} else {
			objectFactory.set("process.id", id);
			Process process = createProcess(processClass, attr, container,
					element, new HashMap<String, String>());
			log.debug("Created Process object: {}", process);
			container.getProcesses().add(process);
		}
	}

	protected Process createProcess(String processClass,
			Map<String, String> attr, ProcessContainer container,
			Element element, Map<String, String> extraVariables)
			throws Exception {
		Process process = (Process) objectFactory.create(processClass, attr);
		log.debug("Created Process object: {}", process);

		List<Processor> procs = createNestedProcessors(container, element,
				extraVariables);
		for (Processor p : procs) {
			process.addProcessor(p);
		}
		return process;
	}

	protected Processor createProcessor(ProcessContainer container,
			Element child, Map<String, String> extraVariables) throws Exception {

		Map<String, String> params = objectFactory.getAttributes(child);

		Object o = objectFactory.create(child);
		if (o instanceof Processor) {

			Map<String, String> vars = new HashMap<String, String>(container
					.getContext().getProperties());
			vars.putAll(extraVariables);

			VariableContext vctx = new VariableContext(vars);
			if (o instanceof ProcessorList) {

				NodeList children = child.getChildNodes();
				for (int i = 0; i < children.getLength(); i++) {

					Node node = children.item(i);
					if (node.getNodeType() == Node.ELEMENT_NODE) {

						Element element = (Element) node;
						Processor proc = createProcessor(container, element,
								extraVariables);
						if (proc != null) {
							((ProcessorList) o).getProcessors().add(proc);
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

					id = vctx.expand(id);

					log.debug(
							"Registering processor with id '{}' in look-up service",
							id);

					container.getContext().register(id, (Service) o);

				} else {
					log.warn(
							"Processor '{}' specifies an ID attribute '{}' but does not implement a Service interface. Processor will *not* be registered!",
							o.getClass().getName(), params.get("id"));
				}
			}

			for (String key : params.keySet()) {

				// remove obsolete "-ref" string, this is to keep
				// backwards-compatibility
				//
				String k = key;

				if (key.endsWith("-ref"))
					key = key.replace("-ref", "");

				Class<? extends Service> serviceClass = ServiceInjection
						.hasServiceSetter(key, o);
				if (serviceClass != null) {
					log.info(
							"Found service setter for key '{}' in processor {}",
							key, o);

					String ref = params.get(k);
					ref = vctx.expand(ref);
					ServiceReference serviceRef = new ServiceReference(ref, o,
							key, serviceClass);
					container.getServiceRefs().add(serviceRef);
					continue;
				}

				/*
				 * if (key.endsWith("-ref")) { String ref = params.get(key); ref
				 * = vctx.expand(ref);
				 * 
				 * if (serviceClass == null) throw new Exception(
				 * "Service interface of referenced service '" + key +
				 * "' cannot be determined!");
				 * 
				 * ServiceReference serviceRef = new ServiceReference(ref, o,
				 * key, serviceClass);
				 * container.getServiceRefs().add(serviceRef); }
				 */
			}

			return (Processor) o;
		}

		return null;
	}

	protected List<Processor> createNestedProcessors(
			ProcessContainer container, Element child,
			Map<String, String> extraVariables) throws Exception {
		List<Processor> procs = new ArrayList<Processor>();

		NodeList pnodes = child.getChildNodes();
		for (int j = 0; j < pnodes.getLength(); j++) {

			Node cnode = pnodes.item(j);
			if (cnode.getNodeType() == Node.ELEMENT_NODE) {
				Processor p = createProcessor(container, (Element) cnode,
						extraVariables);
				if (p != null) {
					log.debug("Found processor...");
					procs.add(p);
				}
			}
		}
		return procs;
	}

}
