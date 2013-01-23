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
import stream.runtime.Variables;
import stream.runtime.setup.ObjectFactory;
import stream.runtime.setup.ParameterInjection;
import backtype.storm.topology.BoltDeclarer;
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
		return name.equalsIgnoreCase("bolt")
				|| name.equalsIgnoreCase("storm:bolt");
	}

	/**
	 * @see stream.storm.config.ConfigHandler#handle(org.w3c.dom.Element,
	 *      stream.StreamTopology, backtype.storm.topology.TopologyBuilder)
	 */
	@Override
	public void handle(Element el, StreamTopology st, TopologyBuilder builder)
			throws Exception {

		if (el.getNodeName().equalsIgnoreCase("bolt")
				|| el.getNodeName().equals("storm:bolt")) {

			String id = el.getAttribute("id");
			if (id == null)
				throw new Exception("Element '" + el.getNodeName()
						+ "' is missing an 'id' attribute!");

			String className = el.getAttribute("class");
			Map<String, String> params = objectFactory.getAttributes(el);

			List<String> inputs = getInputNames(el);
			if (inputs.isEmpty()) {
				throw new RuntimeException("No 'input' defined for bolt '" + id
						+ "' (class '" + className + "')");
			}

			// log.debug(
			// "Creating direct bolt-instance for class '{}', params: {}",
			// className, params);
			IRichBolt bolt = (IRichBolt) objectFactory
					.create(className, params);

			log.info("  > Injecting parameters {} into bolt {}", params, bolt);
			ParameterInjection.inject(bolt, params, new Variables());

			log.info("  > Registering bolt '{}' with instance {}", id, bolt);
			BoltDeclarer boltDeclarer = builder.setBolt(id, bolt);
			BoltDeclarer cur = boltDeclarer;
			for (String input : inputs) {
				log.info("  > Connecting bolt '{}' to shuffle-group '{}'", id,
						input);
				cur = cur.shuffleGrouping(input);
			}

			st.addBolt(id, cur);
		}
	}
}
