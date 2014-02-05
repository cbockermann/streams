package stream.runtime.setup.handler;

import java.util.Map;

import org.w3c.dom.Element;

import stream.ComputeGraph;
import stream.io.Queue;
import stream.runtime.DependencyInjection;
import stream.runtime.ElementHandler;
import stream.runtime.ProcessContainer;
import stream.runtime.setup.factory.ObjectFactory;
import stream.service.Service;
import stream.util.Variables;

public class QueueElementHandler implements ElementHandler {

	/**
	 * @see stream.runtime.ElementHandler#getKey()
	 */
	@Override
	public String getKey() {
		return "Queue";
	}

	/**
	 * @see stream.runtime.ElementHandler#handlesElement(org.w3c.dom.Element)
	 */
	@Override
	public boolean handlesElement(Element element) {
		return getKey().equalsIgnoreCase(element.getNodeName());
	}

	/**
	 * @see stream.runtime.ElementHandler#handleElement(stream.runtime.ProcessContainer,
	 *      org.w3c.dom.Element)
	 */
	@Override
	public void handleElement(ProcessContainer container, Element element,
			Variables variables, DependencyInjection dependencyInjection)
			throws Exception {

		final ComputeGraph computeGraph = container.computeGraph();

		String className = element.getAttribute("class");
		if (className == null || className.trim().isEmpty())
			className = "stream.io.BlockingQueue";

		Map<String, String> params = container.getObjectFactory()
				.getAttributes(element);
		if (!params.containsKey("class")) {
			params.put("class", "stream.io.BlockingQueue");
		}

		String id = element.getAttribute("id");
		if (id == null || id.trim().isEmpty()) 
			throw new Exception("No 'id' attribute defined for queue!");

		// TODO Dieses erzeugen einer Queue und das erzeugen implizieter Queues
		// sollte identisch sein....
		Queue queue = (Queue) container.getObjectFactory().create(className,
				params, ObjectFactory.createConfigDocument(element));
		container.registerQueue(id, queue, true);
		computeGraph.addQueue(id, queue);

		if (queue instanceof Service) {
			container.getContext().register(id, (Service) queue);
			computeGraph.addService(id, (Service) queue);
		}
	}
}