/**
 * 
 */
package stream.storm.config;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import stream.StreamTopology;
import stream.runtime.setup.ObjectFactory;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.SpoutDeclarer;
import backtype.storm.topology.TopologyBuilder;

/**
 * @author chris
 * 
 */
public class SpoutHandler extends ATopologyElementHandler {

	static Logger log = LoggerFactory.getLogger(SpoutHandler.class);

	/**
	 * @param of
	 */
	public SpoutHandler(ObjectFactory of) {
		super(of);
	}

	/**
	 * @see stream.storm.config.ConfigHandler#handles(org.w3c.dom.Element)
	 */
	@Override
	public boolean handles(Element el) {
		if (el == null)
			return false;

		String name = el.getNodeName();
		return "storm:spout".equalsIgnoreCase(name);
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
		if (id == null)
			throw new Exception("Element '" + el.getNodeName()
					+ "' is missing an 'id' attribute!");

		String className = el.getAttribute("class");
		Map<String, String> params = objectFactory.getAttributes(el);

		log.info("  > Found '{}' definition, with class: {}", el.getNodeName(),
				className);
		log.info("  >   Parameters are: {}", params);

		params = st.getVariables().expandAll(params);
		log.info("  >   Expanded parameters: {}", params);

		log.info("  >   Creating spout-instance from class {}, parameters: {}",
				className, params);
		IRichSpout bolt = (IRichSpout) objectFactory.create(className, params, ObjectFactory.createConfigDocument(el));

		log.info("  > Registering spout '{}' with instance {}", id, bolt);
		SpoutDeclarer spoutDeclarer = builder.setSpout(id, bolt);
		SpoutDeclarer cur = spoutDeclarer;

		st.addSpout(id, cur);
	}
}