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
package stream.runtime.setup.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import stream.runtime.DependencyInjection;
import stream.runtime.ProcessContainer;
import stream.runtime.setup.factory.MonitorFactory;
import stream.runtime.setup.factory.ObjectFactory;
import stream.runtime.setup.factory.ProcessConfiguration;
import stream.runtime.setup.factory.ProcessorFactory;
import stream.util.Variables;

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
	 * @see stream.runtime.ElementHandler#getKey()
	 */
	@Override
	public String getKey() {
		return "Monitor";
	}

	/**
	 * @see stream.runtime.ElementHandler#handlesElement(org.w3c.dom .Element)
	 */
	@Override
	public boolean handlesElement(Element element) {
		return "monitor".equalsIgnoreCase(element.getNodeName());
	}

	/**
	 * @see stream.runtime.ElementHandler#handleElement(stream.container.ProcessContainer,
	 *      org.w3c.dom.Element)
	 */
	@Override
	public void handleElement(ProcessContainer container, Element element,
			Variables variables, DependencyInjection dependencyInjection)
			throws Exception {
		MonitorFactory mf = new MonitorFactory(container, objectFactory,
				dependencyInjection);
		ProcessConfiguration[] configs = mf.createConfigurations(element,
				variables);
		mf.createAndRegisterProcesses(configs);
	}

	//
	// Map<String, String> params = objectFactory.getAttributes(element);
	//
	// // the default Monitor class is stream.runtime.Monitor
	// //
	// Map<String, String> attr = objectFactory.getAttributes(element);
	//
	// // List<Processor> procs = createNestedProcessors(container, element,
	// // variables);
	// // for (Processor p : procs)
	// // monitor.add(p);
	// //
	// // container.getProcesses().add(monitor);
	//
	// String copies = attr.get("copies");
	// if (attr.containsKey("multiply")) {
	// copies = attr.get("multiply");
	// log.warn("The attribute 'multiply' is deprecated for element 'Process'");
	// log.warn("Please use 'copies' instead of 'multiply'.");
	// }
	//
	// if (copies != null && !"".equals(copies.trim())) {
	//
	// Variables var = new Variables(variables);
	// log.debug("Expanding '{}'", copies);
	// copies = var.expand(copies);
	//
	// String[] ids;
	// if (copies.indexOf(",") >= 0) {
	// ids = copies.split(",");
	// } else {
	// Integer times = new Integer(copies);
	// ids = new String[times];
	// for (int i = 0; i < times; i++) {
	// ids[i] = "" + i;
	// }
	// }
	// log.debug("Creating {} processes due to copies='{}'", ids.length,
	// copies);
	//
	// for (String pid : ids) {
	// Variables local = new Variables(variables);
	// local.put("monitor.id", pid);
	// local.put("copy.id", pid);
	//
	// String className = "stream.runtime.Monitor";
	// Monitor monitor = (Monitor) objectFactory.create(className,
	// params, ObjectFactory.createConfigDocument(element));
	// List<Processor> procs = createNestedProcessors(container,
	// element, local, dependencyInjection);
	// for (Processor p : procs)
	// monitor.add(p);
	// log.debug("Created Monitor object: {}", monitor);
	// container.getProcesses().add(monitor);
	// }
	//
	// } else {
	// Variables local = new Variables(variables);
	// objectFactory.set("monitor.id", "0");
	// local.put("monitor.id", "0");
	//
	// String className = "stream.runtime.Monitor";
	// Monitor monitor = (Monitor) objectFactory.create(className, params,
	// ObjectFactory.createConfigDocument(element));
	// List<Processor> procs = createNestedProcessors(container, element,
	// local, dependencyInjection);
	// for (Processor p : procs)
	// monitor.add(p);
	// log.debug("Created Monitor object: {}", monitor);
	// container.getProcesses().add(monitor);
	// }
	//
	// }
}
