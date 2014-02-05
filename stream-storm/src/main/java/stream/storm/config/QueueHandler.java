/**
 * 
 */
package stream.storm.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import stream.StreamTopology;
import stream.runtime.setup.factory.ObjectFactory;
import stream.storm.QueueBolt;
import backtype.storm.topology.BoltDeclarer;
import backtype.storm.topology.TopologyBuilder;

/**
 * @author chris
 * 
 */
public class QueueHandler extends ATopologyElementHandler {

	static Logger log = LoggerFactory.getLogger(QueueHandler.class);
	final String xml;

	/**
	 * @param of
	 */
	public QueueHandler(ObjectFactory of, String xml) {
		super(of);
		this.xml = xml;
	}

	/**
	 * @see stream.storm.config.ConfigHandler#handles(org.w3c.dom.Element)
	 */
	@Override
	public boolean handles(Element el) {
		return "queue".equalsIgnoreCase(el.getNodeName());
	}

	/**
	 * @see stream.storm.config.ConfigHandler#handle(org.w3c.dom.Element,
	 *      stream.StreamTopology, backtype.storm.topology.TopologyBuilder)
	 */
	@Override
	public void handle(Element element, StreamTopology st,
			TopologyBuilder builder) throws Exception {

		String id = element.getAttribute("id");
		if (id == null || id.trim().isEmpty())
			throw new Exception(
					"Queue element does not specify 'id' attribute!");

		QueueBolt bolt = new QueueBolt(xml, id);
		log.info("  >   Registering bolt (queue) '{}' with instance {}", id,
				bolt);

		BoltDeclarer boltDeclarer = builder.setBolt(id, bolt, 1);
		BoltDeclarer cur = boltDeclarer;
		log.debug("  >  Adding queue to stream-topology...");
		st.addBolt(id, cur);
	}
}
