/**
 * 
 */
package stream.runtime.setup;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import stream.data.DataProcessorList;
import stream.data.Processor;

/**
 * @author chris
 * 
 */
public class ProcessorFactory {

	static Logger log = LoggerFactory.getLogger(ProcessorFactory.class);

	ObjectFactory objectFactory;

	public ProcessorFactory(ObjectFactory of) {
		this.objectFactory = of;
	}

	protected List<Processor> createNestedProcessors(Element child)
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

	protected Processor createProcessor(Element child) throws Exception {

		Object o = objectFactory.create(child);
		if (o instanceof Processor) {

			if (o instanceof DataProcessorList) {

				NodeList children = child.getChildNodes();
				for (int i = 0; i < children.getLength(); i++) {

					Node node = children.item(i);
					if (node.getNodeType() == Node.ELEMENT_NODE) {

						Element element = (Element) node;
						Processor proc = createProcessor(element);
						if (proc != null) {
							((DataProcessorList) o).addDataProcessor(proc);
						} else {
							log.warn(
									"Nested element {} is not of type 'stream.data.Processor': ",
									node.getNodeName());
						}
					}
				}
			}

			return (Processor) o;
		}

		return null;
	}
}
