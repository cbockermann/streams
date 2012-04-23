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

import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import stream.Processor;
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
		String urlParam = params.get("url");
		DataStream stream;

		if (urlParam != null) {
			Constructor<?> constr = clazz.getConstructor(URL.class);
			URL url = null;

			String urlString = params.get("url");
			urlString = objectFactory.expand(urlString);

			if (urlString.startsWith("classpath:")) {
				String resource = urlParam.substring("classpath:".length());
				log.debug("Looking up resource '{}'", resource);
				url = ProcessContainer.class.getResource(resource);
				if (url == null) {
					throw new Exception(
							"Classpath url does not exist! Resource '"
									+ resource + "' not found!");
				}
			} else {
				url = new URL(urlString);
			}

			stream = (DataStream) constr.newInstance(url);
		} else {
			Constructor<?> constr = clazz.getConstructor();
			stream = (DataStream) constr.newInstance(new Object[0]);
		}

		List<Processor> preProcessors = processorFactory
				.createNestedProcessors(node);
		for (Processor p : preProcessors) {
			stream.getPreprocessors().add(p);
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
		DataStream stream = null;
		String urlParam = params.get("url");
		if (urlParam == null) {
			log.debug("No 'url' parameter for data class {} found, checking for no-args constructor");
			try {
				stream = (DataStream) clazz.newInstance();
				ParameterInjection
						.inject(stream, params, new VariableContext());
				return stream;
			} catch (Exception e) {
				log.error(
						"No no-args constructor found and no 'url' parameter specified for stream {}!",
						clazz);
				throw new Exception(
						"No no-args constructor found and no 'url' parameter specified for stream "
								+ clazz + "!");
			}
		}

		URL url = null;
		Constructor<?> constr = clazz.getConstructor(URL.class);

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

		stream = (DataStream) constr.newInstance(url);
		ParameterInjection.inject(stream, params, new VariableContext());

		if (stream instanceof MultiDataStream) {
			log.debug("Found a multi-stream, need to add inner streams...");
		}

		return stream;
	}
}
