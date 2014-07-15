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
package stream.storm.config;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import stream.StreamTopology;
import stream.runtime.setup.factory.ObjectFactory;
import stream.storm.StreamSpout;
import stream.util.XMLElementMatch;
import backtype.storm.topology.SpoutDeclarer;
import backtype.storm.topology.TopologyBuilder;

/**
 * @author chris
 * 
 */
public class StreamHandler extends ATopologyElementHandler {

	static Logger log = LoggerFactory.getLogger(StreamHandler.class);
	final String xml;

	/**
	 * @param of
	 */
	public StreamHandler(ObjectFactory of, String xml) {
		super(of);
		this.xml = xml;
	}

	/**
	 * @see stream.storm.config.ConfigHandler#handles(org.w3c.dom.Element)
	 */
	@Override
	public boolean handles(Element el) {
		String name = el.getNodeName();
		return name.equalsIgnoreCase("stream");
	}

	/**
	 * @see stream.storm.config.ConfigHandler#handle(org.w3c.dom.Element,
	 *      stream.StreamTopology, backtype.storm.topology.TopologyBuilder)
	 */
	@Override
	public void handle(Element el, StreamTopology st, TopologyBuilder builder)
			throws Exception {

		if (!handles(el))
			return;

		String id = el.getAttribute("id");
		if (id == null || id.trim().isEmpty()) {
			log.error(
					"Missing attribute 'id' for element 'stream' with class '{}'!",
					el.getAttribute("class"));
			throw new Exception("Missing 'id' attribute for element 'stream'!");
		}

		log.info("  > Creating stream-spout with id '{}'", id);
		String className = el.getAttribute("class");
		log.info("  >   stream-class is: {}", className);

		// Extract the parameters for the stream from the element
		//
		Map<String, String> params = ObjectFactory.newInstance().getAttributes(
				el);
		log.info("  >   stream-parameters are: {}", params);

		// expand any static place-holders (e.g. "${var}") using the
		// properties found in the topology properties
		//
		params = st.getVariables().expandAll(params);
		log.info("  >   expanded stream-parameters are: {}", params);

		StreamSpout spout = new StreamSpout(xml, id, className, params);
		log.info("  >   stream-spout instance is: {}", spout);

		SpoutDeclarer spoutDeclarer = builder.setSpout(id, spout);
		log.info("  >   declared spout is: {}", spoutDeclarer);
		st.spouts.put(id, spoutDeclarer);
	}

	public static class StreamFinder implements XMLElementMatch {
		final String id;

		public StreamFinder(String id) {
			this.id = id;
		}

		/**
		 * @see stream.util.XMLElementMatch#matches(org.w3c.dom.Element)
		 */
		@Override
		public boolean matches(Element el) {
			return "stream".equalsIgnoreCase(el.getNodeName())
					&& id.equals(el.getAttribute("id"));
		}
	}
}