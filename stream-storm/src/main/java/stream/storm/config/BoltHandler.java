/**
 * 
 */
package stream.storm.config;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import stream.StreamTopology;
import stream.runtime.setup.factory.ObjectFactory;
import backtype.storm.topology.BoltDeclarer;
import backtype.storm.topology.IBasicBolt;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.TopologyBuilder;

/**
 * @author chris
 * 
 */
public class BoltHandler extends ATopologyElementHandler {

	static Logger log = LoggerFactory.getLogger(BoltHandler.class);

	public BoltHandler(ObjectFactory of) {
		super(of);
	}

	/**
	 * @see stream.storm.config.ConfigHandler#handles(org.w3c.dom.Element)
	 */
	@Override
	public boolean handles(Element el) {
		String name = el.getNodeName();
		return name.equalsIgnoreCase("storm:bolt");
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

		// log.debug(
		// "Creating direct bolt-instance for class '{}', params: {}",
		// className, params);
		log.info("  >   Creating bolt-instance from class {}, parameters: {}",
				className, params);

		Object obj = objectFactory.create(className, params,
				ObjectFactory.createConfigDocument(el));

		BoltDeclarer boltDeclarer = null;

		if (obj instanceof IRichBolt) {
			IRichBolt bolt = (IRichBolt) obj;
			log.info("  > Registering bolt '{}' with instance {}", id, bolt);
			boltDeclarer = builder.setBolt(id, bolt);
		}

		if (obj instanceof IBasicBolt) {
			IBasicBolt bolt = (IBasicBolt) obj;
			log.info("  > Registering bolt '{}' with instance {}", id, bolt);
			boltDeclarer = builder.setBolt(id, bolt);
		}

		if (boltDeclarer == null) {
			log.debug(
					"Bolt-class '{}' does not implement supported interface (only IRichBolt/IBasicBolt are supported)!",
					className);
			throw new Exception(
					"Bolt-class does not implement supported interface (only IRichBolt/IBasicBolt are supported)!");
		}

		BoltDeclarer cur = boltDeclarer;
		List<String> inputs = getInputNames(el);
		if (!inputs.isEmpty()) {
			for (String input : inputs) {
				if (!input.isEmpty()) {
					log.info("  > Connecting bolt '{}' to shuffle-group '{}'",
							id, input);
					cur = cur.shuffleGrouping(input);
				}
			}
		} else {
			log.debug("No inputs defined for bolt '{}'!", id);
		}

		st.addBolt(id, cur);
	}
}
