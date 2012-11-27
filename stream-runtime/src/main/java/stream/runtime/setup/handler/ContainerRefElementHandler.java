/**
 * 
 */
package stream.runtime.setup.handler;

import java.net.URI;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import stream.runtime.ElementHandler;
import stream.runtime.ProcessContainer;
import stream.runtime.Variables;
import stream.runtime.rpc.RMIClient;
import stream.runtime.setup.ObjectFactory;

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
			Variables variables) throws Exception {

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
