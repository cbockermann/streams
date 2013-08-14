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
package stream.runtime.setup.handler;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import stream.ComputeGraph;
import stream.io.Stream;
import stream.runtime.DependencyInjection;
import stream.runtime.ElementHandler;
import stream.runtime.ProcessContainer;
import stream.runtime.Variables;
import stream.runtime.setup.ObjectFactory;
import stream.runtime.setup.ProcessorFactory;
import stream.runtime.setup.StreamFactory;
import stream.service.Service;

/**
 * @author chris
 * 
 */
public class StreamElementHandler implements ElementHandler {

	static Logger log = LoggerFactory.getLogger(StreamElementHandler.class);
	final ObjectFactory objectFactory;
	final ProcessorFactory processorFactory;

	public StreamElementHandler(ObjectFactory objectFactory) {
		this.objectFactory = objectFactory;
		this.processorFactory = new ProcessorFactory(objectFactory);
	}

	/**
	 * @see stream.runtime.ElementHandler#getKey()
	 */
	@Override
	public String getKey() {
		return "Stream";
	}

	/**
	 * @see stream.runtime.ElementHandler#handlesElement(org.w3c.dom.Element)
	 */
	@Override
	public boolean handlesElement(Element element) {
		if (element == null)
			return false;

		return "Stream".equalsIgnoreCase(element.getNodeName())
				|| "DataStream".equalsIgnoreCase(element.getNodeName());
	}

	/**
	 * @see stream.runtime.ElementHandler#handleElement(stream.runtime.ProcessContainer
	 *      , org.w3c.dom.Element)
	 */
	@Override
	public void handleElement(ProcessContainer container, Element element,
			Variables variables, DependencyInjection dependencyInjection)
			throws Exception {
		try {
			final ComputeGraph computeGraph = container.computeGraph();
			Map<String, String> attr = objectFactory.getAttributes(element);
			String id = attr.get("id");

			List<String> cp = new ArrayList<String>();
			String copies = element.getAttribute("copies");
			if (copies == null || copies.trim().isEmpty()) {
				cp.add(id);
			} else {
				String[] t = copies.split(",");
				for (String c : t) {
					if (!c.trim().isEmpty()) {
						cp.add(c.trim());
					}
				}
			}

			for (String sid : cp) {
				log.info("Creating stream for copy '{}'", sid);
				Variables local = new Variables(variables);
				local.put("copy.id", sid);
				String lid = local.expand(sid);
				Stream stream = StreamFactory.createStream(objectFactory,
						element, variables);
				if (stream != null) {
					if (lid == null)
						lid = "" + stream;
					stream.setId(lid);
					container.setStream(lid, stream);
					computeGraph.addStream(lid, stream);
				}

				if (stream instanceof Service) {
					container.getContext().register(lid, (Service) stream);
				}
			}
		} catch (FileNotFoundException fnfe) {
			throw new Exception("Cannot create stream from referenced file: "
					+ fnfe.getMessage());
		} catch (Exception e) {

			if (e.getCause() != null)
				throw new Exception(e.getCause());

			log.error("Failed to create stream-object: {}", e.getMessage());
			e.printStackTrace();
			throw new Exception("Failed to create data-stream: "
					+ e.getMessage());
		}
	}
}