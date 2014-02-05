/**
 * 
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