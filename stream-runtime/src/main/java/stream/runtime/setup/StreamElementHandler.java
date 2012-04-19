/**
 * 
 */
package stream.runtime.setup;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import stream.io.DataStream;
import stream.runtime.ElementHandler;
import stream.runtime.ProcessContainer;

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
	public void handleElement(ProcessContainer container, Element element)
			throws Exception {
		try {
			Map<String, String> attr = objectFactory.getAttributes(element);
			String id = attr.get("id");

			DataStream stream = DataStreamFactory.createStream(objectFactory,
					processorFactory, element);
			if (stream != null) {
				if (id == null)
					id = "" + stream;
				container.setStream(id, stream);
			}

		} catch (Exception e) {
			log.error("Failed to create object: {}", e.getMessage());
			e.printStackTrace();
		}
	}
}