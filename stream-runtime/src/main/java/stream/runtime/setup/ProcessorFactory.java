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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import stream.Processor;
import stream.ProcessorList;

/**
 * @author chris
 * 
 */
public class ProcessorFactory {

	static Logger log = LoggerFactory.getLogger(ProcessorFactory.class);

	ObjectFactory objectFactory;
	final List<ProcessorCreationHandler> handlers = new ArrayList<ProcessorCreationHandler>();

	public ProcessorFactory(ObjectFactory of) {
		this.objectFactory = of;
	}

	public void addCreationHandler(ProcessorCreationHandler h) {
		if (!handlers.contains(h))
			handlers.add(h);
	}

	public void removeCreationHandler(ProcessorCreationHandler h) {
		handlers.remove(h);
	}

	public List<Processor> createNestedProcessors(Element child)
			throws Exception {
		List<Processor> procs = new ArrayList<Processor>();

		NodeList pnodes = child.getChildNodes();
		for (int j = 0; j < pnodes.getLength(); j++) {

			Node cnode = pnodes.item(j);
			if (cnode.getNodeType() == Node.ELEMENT_NODE) {
				Processor p = createProcessor((Element) cnode);
				if (p != null) {
					log.debug("Found processor...");
					procs.add(p);
				}
			}
		}
		return procs;
	}

	public Processor createProcessor(Element child) throws Exception {

		Object o = objectFactory.create(child);
		if (o instanceof Processor) {

			if (o instanceof ProcessorList) {

				NodeList children = child.getChildNodes();
				for (int i = 0; i < children.getLength(); i++) {

					Node node = children.item(i);
					if (node.getNodeType() == Node.ELEMENT_NODE) {

						Element element = (Element) node;
						Processor proc = createProcessor(element);
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

			for (ProcessorCreationHandler handler : this.handlers) {
				try {
					handler.processorCreated((Processor) o, child);
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException(e.getMessage());
				}
			}

			return (Processor) o;
		}

		return null;
	}

	public static interface ProcessorCreationHandler {
		public void processorCreated(Processor p, Element from)
				throws Exception;
	}
}
