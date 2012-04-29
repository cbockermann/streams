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