package stream.runtime.setup.handler;

import java.util.Map;
import java.util.MissingResourceException;

import org.w3c.dom.Element;

import stream.ComputeGraph;
import stream.io.Queue;
import stream.io.Sink;
import stream.runtime.DependencyInjection;
import stream.runtime.ElementHandler;
import stream.runtime.ProcessContainer;
import stream.runtime.Variables;
import stream.runtime.setup.ObjectFactory;
import stream.service.Service;

public class SinkElementHandler implements ElementHandler {

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
			throw new IllegalArgumentException("class attribute is missing ");

		Map<String, String> params = container.getObjectFactory()
				.getAttributes(element);
		if (!params.containsKey("class")) 
			throw new IllegalArgumentException("class attribute is missing ");

		String id = element.getAttribute("id");
		if (id == null || id.trim().isEmpty()) 
			throw new IllegalArgumentException("No 'id' attribute defined for sink!");

		Sink sink = (Queue) container.getObjectFactory().create(className,
				params, ObjectFactory.createConfigDocument(element));
		
		container.registerSink(id, sink);
		computeGraph.addSink(id,sink);

		
	}
}