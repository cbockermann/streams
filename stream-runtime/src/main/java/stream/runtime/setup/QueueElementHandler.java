package stream.runtime.setup;

import java.util.Map;

import org.w3c.dom.Element;

import stream.io.BlockingQueue;
import stream.runtime.ElementHandler;
import stream.runtime.ProcessContainer;

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
	public void handleElement(ProcessContainer container, Element element)
			throws Exception {

		String className = element.getAttribute("class");
		if (className == null || className.trim().isEmpty())
			className = "stream.io.BlockingQueue";

		Map<String, String> params = container.getObjectFactory()
				.getAttributes(element);
		if (!params.containsKey("class")) {
			params.put("class", "stream.io.BlockingQueue");
		}

		String id = element.getAttribute("id");
		if (id == null || id.trim().isEmpty()) {
			throw new Exception("No 'id' attribute defined for queue!");
		}

		BlockingQueue queue = (BlockingQueue) container.getObjectFactory()
				.create(className, params);
		container.registerQueue(id, queue, true);
	}
}