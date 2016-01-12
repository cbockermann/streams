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

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import stream.app.ComputeGraph.SourceRef;
import stream.runtime.DependencyInjection;
import stream.runtime.ElementHandler;
import stream.runtime.LifeCycle;
import stream.runtime.ProcessContainer;
import stream.runtime.setup.factory.ObjectFactory;
import stream.service.Service;
import stream.util.Variables;

/**
 * <p>
 * This class handles XML <code>Service</code> elements and will create and
 * register the corresponding elements as standalone service providers within
 * the process container.
 * </p>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public class ServiceElementHandler implements ElementHandler {

	static Logger log = LoggerFactory.getLogger(ServiceElementHandler.class);
	final ObjectFactory objectFactory;

	public ServiceElementHandler(ObjectFactory factory) {
		this.objectFactory = factory;
	}

	/**
	 * @see stream.runtime.ElementHandler#getKey()
	 */
	@Override
	public String getKey() {
		return "Service";
	}

	/**
	 * @see stream.runtime.ElementHandler#handlesElement(org.w3c.dom.Element)
	 */
	@Override
	public boolean handlesElement(Element element) {
		if (element == null)
			return false;

		return "service".equalsIgnoreCase(element.getNodeName());
	}

	/**
	 * @see stream.runtime.ElementHandler#handleElement(stream.container.ProcessContainer
	 *      , org.w3c.dom.Element)
	 */
	@Override
	public void handleElement(ProcessContainer container, Element element,
			Variables variables, DependencyInjection dependencyInjection)
			throws Exception {

		log.debug("handling element {}...", element);
		Map<String, String> params = objectFactory.getAttributes(element);

		String className = params.get("class");
		if (className == null || "".equals(className.trim())) {
			throw new Exception(
					"No class name provided in 'class' attribute if Service element!");
		}

		String id = params.get("id");
		if (id == null || "".equals(id.trim())) {
			throw new Exception(
					"No valid 'id' attribute provided for Service element!");
		} else {
			id = id.trim();
		}

		log.debug("Creating new service implementation from class {}",
				className);
		try {
			Service service = (Service) objectFactory.create(className, params,
					ObjectFactory.createConfigDocument(element), variables);
			service.reset();
			container.getContext().register(id, service);
			if (service instanceof LifeCycle) {
				container.register((LifeCycle) service);
			}
			String input = params.get("input");

			if (input != null && !input.trim().isEmpty()) {
				SourceRef sourceRef = new SourceRef(service, "input", input);
				dependencyInjection.add(sourceRef);
				// this should not be required in the future - handled
				// by dependencyInjection class
				// computeGraph.addReference(sourceRef);
			}
		} catch (Exception e) {
			log.error("Failed to create and register service '{}': {}", id,
					e.getMessage());
			throw e;
		}
	}
}
