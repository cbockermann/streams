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

import java.util.HashMap;
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

		List<Processor> procs = createNestedProcessors(container, element,
				new HashMap<String, String>());
		for (Processor p : procs)
			monitor.addProcessor(p);

		container.getProcesses().add(monitor);
	}
}
