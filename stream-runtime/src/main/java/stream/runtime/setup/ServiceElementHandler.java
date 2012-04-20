/**
 * 
 */
package stream.runtime.setup;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import stream.runtime.ElementHandler;
import stream.runtime.ProcessContainer;
import stream.service.Service;

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
	 * @see stream.runtime.ElementHandler#handleElement(stream.runtime.ProcessContainer
	 *      , org.w3c.dom.Element)
	 */
	@Override
	public void handleElement(ProcessContainer container, Element element)
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
			Service service = (Service) objectFactory.create(className, params);
			service.reset();
			container.getContext().register(id, service);
		} catch (Exception e) {
			log.error("Failed to create and register service '{}': {}", id,
					e.getMessage());
			throw e;
		}
	}
}