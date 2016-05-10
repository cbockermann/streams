/*
 *  streams library
 *
 *  Copyright (C) 2011-2014 by Christian Bockermann, Hendrik Blom
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
package stream.runtime.setup.factory;

import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import stream.io.SourceURL;
import stream.io.Stream;
import stream.io.multi.MultiStream;
import stream.runtime.ProcessContainer;
import stream.runtime.setup.ParameterInjection;
import stream.util.Variables;

/**
 * @author chris,Hendrik
 * 
 */
public class StreamFactory {

	static Logger log = LoggerFactory.getLogger(StreamFactory.class);

	final static Map<String, String> streamClassesByExtension = new LinkedHashMap<String, String>();

	static {
		streamClassesByExtension.put("csv", "stream.io.CsvStream");
		streamClassesByExtension.put("txt", "stream.io.LineStream");
		streamClassesByExtension.put("svmlight", "stream.io.SvmLightStream");
		streamClassesByExtension.put("json", "stream.io.JSONStream");
		streamClassesByExtension.put("arff", "stream.io.ArffStream");
	}

	public static Stream createStream(ObjectFactory objectFactory, Element node, Variables local) throws Exception {
		Map<String, String> params = objectFactory.getAttributes(node);

		Class<?> clazz = Class.forName(local.expand(params.get("class")));
		String urlParam = params.get("url");
		if (urlParam != null && clazz == null) {

		}

		Stream stream;

		if (urlParam != null) {
			Constructor<?> constr = clazz.getConstructor(SourceURL.class);
			SourceURL url = null;

			String urlString = params.get("url");
			urlString = local.expand(urlString);

			if (urlString.startsWith("classpath:")) {
				String resource = urlString.substring("classpath:".length());
				log.debug("Looking up resource '{}'", resource);
				URL u = ProcessContainer.class.getResource(resource);
				if (u == null) {
					throw new Exception("Classpath url does not exist! Resource '" + resource + "' not found!");
				}
				url = new SourceURL(u);
			} else {
				url = new SourceURL(urlString);
			}

			stream = (Stream) constr.newInstance(url);
		} else {
			Constructor<?> constr = clazz.getConstructor();
			stream = (Stream) constr.newInstance(new Object[0]);
		}

		params = local.expandAll(params);
		log.debug("Injecting variables {} into stream {}", params, stream);
		ParameterInjection.inject(stream, params, local);

		if (stream instanceof MultiStream) {
			MultiStream multiStream = (MultiStream) stream;
			log.debug("Found a multi-stream, need to add inner streams...");

			NodeList nodes = node.getChildNodes();
			for (int i = 0; i < nodes.getLength(); i++) {
				Node inner = nodes.item(i);
				if (inner.getNodeType() != Node.ELEMENT_NODE)
					continue;

				Element child = (Element) inner;

				if (child.getNodeName().equalsIgnoreCase("stream")
						|| child.getNodeName().equalsIgnoreCase("datastream")) {
					Stream innerStream = createStream(objectFactory, child, local);
					log.debug("Created inner stream {}", innerStream);
					String id = child.getAttribute("id");
					if (id == null || "".equals(id.trim()))
						id = innerStream.toString();
					multiStream.addStream(id, innerStream);
				} else {
					throw new Exception("Pre-processors within streams are no longer supported!");
				}
			}
			stream = multiStream;
		}

		return stream;
	}

	public static Class<?> guessStreamFormat(String url) throws Exception {

		log.debug("Trying to derive stream class from URL '{}'", url);
		String u = url.toLowerCase();

		boolean gz = u.endsWith(".gz");
		String ext = null;

		if (gz) {
			u = u.replaceAll("\\.gz$", "");
		}

		int idx = u.lastIndexOf(".");
		if (idx > 0) {
			ext = u.substring(idx + 1);
			log.debug("Extension of URL is '{}'", ext);
			String className = streamClassesByExtension.get(ext);
			if (className != null)
				return Class.forName(className);
		}

		return null;
	}
}