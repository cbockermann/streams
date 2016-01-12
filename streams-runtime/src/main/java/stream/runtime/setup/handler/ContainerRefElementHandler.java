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

import java.net.URI;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import stream.runtime.DependencyInjection;
import stream.runtime.ElementHandler;
import stream.runtime.ProcessContainer;
import stream.runtime.rpc.RMIClient;
import stream.runtime.setup.factory.ObjectFactory;
import stream.util.Variables;

/**
 * @author chris
 * 
 */
public class ContainerRefElementHandler implements ElementHandler {

	static Logger log = LoggerFactory
			.getLogger(ContainerRefElementHandler.class);
	final ObjectFactory objectFactory;

	public ContainerRefElementHandler(ObjectFactory of) {
		this.objectFactory = of;
	}

	/**
	 * @see stream.runtime.ElementHandler#getKey()
	 */
	@Override
	public String getKey() {
		return "container-ref";
	}

	/**
	 * @see stream.runtime.ElementHandler#handlesElement(org.w3c.dom.Element)
	 */
	@Override
	public boolean handlesElement(Element element) {
		return getKey().equalsIgnoreCase(element.getNodeName());
	}

	/**
	 * @see stream.runtime.ElementHandler#handleElement(stream.runtime.ProcessContainer
	 *      , org.w3c.dom.Element)
	 */
	@Override
	public void handleElement(ProcessContainer container, Element element,
			Variables variables, DependencyInjection dependencyInjection)
			throws Exception {

		Map<String, String> attributes = objectFactory.getAttributes(element);
		String id = "" + attributes.get("id");
		if (attributes.containsKey("name"))
			id = attributes.get("name");

		String url = attributes.get("url");
		if (url != null) {
			URI uri = new URI(url);
			String proto = uri.getScheme();
			log.debug("  proto: {}", proto);
			log.debug("  host: {}", uri.getHost());
			log.debug("  port: {}", uri.getPort());

			if ("rmi".equalsIgnoreCase(proto)) {
				log.info("Adding remote RMI container connection...");
				RMIClient client = new RMIClient(uri.getHost(), new Integer(
						uri.getPort()));
				container.getContext().addContainer(id, client);
				return;
			}

			throw new Exception("Protocol '" + proto
					+ "' not supported for remote containers!");

		} else {
			log.error("Missing attribute 'url' for element container-ref!");
			throw new Exception(
					"Missing attribute 'url' for element container-ref!");
		}
	}
}
