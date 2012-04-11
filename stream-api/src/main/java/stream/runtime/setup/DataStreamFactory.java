/**
 * 
 */
package stream.runtime.setup;

import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import stream.data.Processor;
import stream.io.DataStream;
import stream.io.multi.MultiDataStream;
import stream.runtime.ProcessContainer;
import stream.runtime.VariableContext;

/**
 * @author chris
 * 
 */
public class DataStreamFactory {

	static Logger log = LoggerFactory.getLogger(DataStreamFactory.class);

	public static DataStream createStream(ObjectFactory objectFactory,
			ProcessorFactory processorFactory, Element node) throws Exception {
		Map<String, String> params = objectFactory.getAttributes(node);
		Class<?> clazz = Class.forName(params.get("class"));
		Constructor<?> constr = clazz.getConstructor(URL.class);
		String urlParam = params.get("url");
		URL url = null;

		if (params.get("url").startsWith("classpath:")) {
			String resource = urlParam.substring("classpath:".length());
			log.debug("Looking up resource '{}'", resource);
			url = ProcessContainer.class.getResource(resource);
			if (url == null) {
				throw new Exception("Classpath url does not exist! Resource '"
						+ resource + "' not found!");
			}
		} else {
			url = new URL(urlParam);
		}

		DataStream stream = (DataStream) constr.newInstance(url);

		List<Processor> preProcessors = processorFactory
				.createNestedProcessors(node);
		for (Processor p : preProcessors) {
			stream.addPreprocessor(p);
		}

		ParameterInjection.inject(stream, params, new VariableContext());

		if (stream instanceof MultiDataStream) {
			MultiDataStream multiStream = (MultiDataStream) stream;
			log.debug("Found a multi-stream, need to add inner streams...");

			NodeList nodes = node.getChildNodes();
			for (int i = 0; i < nodes.getLength(); i++) {
				Node inner = nodes.item(i);
				if (inner.getNodeType() == Node.ELEMENT_NODE) {
					Element child = (Element) inner;
					DataStream innerStream = createStream(objectFactory,
							processorFactory, child);
					log.debug("Created inner stream {}", innerStream);
					String id = child.getAttribute("id");
					if (id == null || "".equals(id.trim()))
						id = innerStream.toString();
					multiStream.addStream(id, innerStream);
				}
			}
		}

		return stream;
	}

	/**
	 * 
	 * @deprecated
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static DataStream createStream(Map<String, String> params)
			throws Exception {
		Class<?> clazz = Class.forName(params.get("class"));
		Constructor<?> constr = clazz.getConstructor(URL.class);
		String urlParam = params.get("url");
		URL url = null;

		if (params.get("url").startsWith("classpath:")) {
			String resource = urlParam.substring("classpath:".length());
			log.debug("Looking up resource '{}'", resource);
			url = ProcessContainer.class.getResource(resource);
			if (url == null) {
				throw new Exception("Classpath url does not exist! Resource '"
						+ resource + "' not found!");
			}
		} else {
			url = new URL(urlParam);
		}

		DataStream stream = (DataStream) constr.newInstance(url);
		ParameterInjection.inject(stream, params, new VariableContext());

		if (stream instanceof MultiDataStream) {
			log.debug("Found a multi-stream, need to add inner streams...");
		}

		return stream;
	}
}
