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

import stream.CopiesUtils;
import stream.Copy;
import stream.app.ComputeGraph;
import stream.io.Sink;
import stream.runtime.DependencyInjection;
import stream.runtime.ElementHandler;
import stream.runtime.ProcessContainer;
import stream.runtime.setup.factory.ObjectFactory;
import stream.util.Variables;

/**
 * @author Hendrik,cris
 * 
 */
public class SinkElementHandler implements ElementHandler {

	static Logger log = LoggerFactory.getLogger(ProcessElementHandler.class);

	/**
	 * @see stream.runtime.ElementHandler#getKey()
	 */
	@Override
	public String getKey() {
		return "Sink";
	}

	/**
	 * @see stream.runtime.ElementHandler#handlesElement(org.w3c.dom.Element)
	 */
	@Override
	public boolean handlesElement(Element element) {
		return getKey().equalsIgnoreCase(element.getNodeName());
	}

	/**
	 * @see stream.runtime.ElementHandler#handleElement(stream.container.ProcessContainer,
	 *      org.w3c.dom.Element)
	 */
	@Override
	public void handleElement(ProcessContainer container, Element element,
			Variables variables, DependencyInjection dependencyInjection)
			throws Exception {

		String id = element.getAttribute("id");
		if (id == null || id.trim().isEmpty())
			throw new IllegalArgumentException(
					"No 'id' attribute defined for sink!");

		String copiesString = element.getAttribute("copies");
		Copy[] copies = null;
		if (copiesString != null && !copiesString.isEmpty()) {
			copiesString = variables.expand(copiesString);
			copies = CopiesUtils.parse(copiesString);
		} else {
			Copy c = new Copy();
			c.setId(id);
			copies = new Copy[] { c };
		}

		if (copies == null) {
			log.info("queues where not created, due to 'zero' copies");
			return;
		}

		final ComputeGraph computeGraph = container.computeGraph();

		String className = element.getAttribute("class");
		if (className == null || className.trim().isEmpty())
			throw new IllegalArgumentException("class attribute is missing ");

		Map<String, String> params = container.getObjectFactory()
				.getAttributes(element);
		if (!params.containsKey("class"))
			throw new IllegalArgumentException("class attribute is missing ");

		for (Copy copy : copies) {
			Variables local = new Variables(variables);
			CopiesUtils.addCopyIds(local, copy);
			String cid = local.expand(id);
			Sink sink = (Sink) container.getObjectFactory().create(className,
					params, ObjectFactory.createConfigDocument(element), local);

			container.registerSink(cid, sink);
			log.info("register sink: {}", cid);
			computeGraph.addSink(cid, sink);
			log.info("add sink to compute graph: {}", cid);
		}

	}
}