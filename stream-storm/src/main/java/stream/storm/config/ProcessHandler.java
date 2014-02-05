/**
 * 
 */
package stream.storm.config;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import stream.StreamTopology;
import stream.Subscription;
import stream.runtime.setup.factory.ObjectFactory;
import stream.storm.ProcessBolt;
import backtype.storm.topology.BoltDeclarer;
import backtype.storm.topology.TopologyBuilder;

/**
 * @author chris
 * 
 */
public class ProcessHandler extends ATopologyElementHandler {

	static Logger log = LoggerFactory.getLogger(ProcessHandler.class);

	final String xml; // the xml string (config)

	/**
	 * @param of
	 */
	public ProcessHandler(ObjectFactory of, String xml) {
		super(of);
		this.xml = xml;
	}

	/**
	 * @see stream.storm.config.ConfigHandler#handles(org.w3c.dom.Element)
	 */
	@Override
	public boolean handles(Element el) {
		String name = el.getNodeName();
		return name.equalsIgnoreCase("process");
	}

	/**
	 * @see stream.storm.config.ConfigHandler#handle(org.w3c.dom.Element,
	 *      stream.StreamTopology, backtype.storm.topology.TopologyBuilder)
	 */
	@Override
	public void handle(Element el, StreamTopology st, TopologyBuilder builder)
			throws Exception {

		if (el.getNodeName().equalsIgnoreCase("process")) {
			String id = el.getAttribute("id");
			if (id == null || id.trim().isEmpty()) {
				log.error(
						"No 'id' attribute defined in process element (class: '{}')",
						el.getAttribute("class"));
				throw new Exception(
						"Missing 'id' attribute for process element!");
			}

			log.info("  > Creating process-bolt with id '{}'", id);

			String copies = el.getAttribute("copies");
			Integer workers = 1;
			String input = el.getAttribute("input");
			List<String> inputs = getInputNames(el);

			if (copies != null && !copies.isEmpty()) {
				try {
					workers = Integer.parseInt(copies);
				} catch (Exception e) {
					workers = 1;
					throw new RuntimeException("Invalid number of copies '"
							+ copies + "' specified!");
				}
			}

			log.info("  >   Adding bolt '{}', subscribing to input(s): '{}'",
					id, input);

			ProcessBolt bolt = new ProcessBolt(xml, id, st.getVariables());
			log.info("  >   Registering bolt (process) '{}' with instance {}",
					id, bolt);

			BoltDeclarer boltDeclarer = builder.setBolt(id, bolt, workers);

			BoltDeclarer cur = boltDeclarer;
			if (!inputs.isEmpty()) {
				for (String in : inputs) {

					if (!in.isEmpty()) {

						//
						// if 'in' is reference to a process/bolt
						//

						//
						// else
						//
						log.info(
								"  >   Connecting bolt '{}' to non-group '{}'",
								id, in);
						cur = cur.noneGrouping(in);
					}
				}
			} else {
				log.warn("No input defined for process '{}'!", id);
			}
			st.addBolt(id, cur);

			for (Subscription subscription : bolt.getSubscriptions()) {
				log.info("Adding subscription:  {}", subscription);
				st.addSubscription(subscription);
			}
		}
	}
}