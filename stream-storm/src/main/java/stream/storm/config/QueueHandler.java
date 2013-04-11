/**
 * 
 */
package stream.storm.config;

import org.w3c.dom.Element;

import stream.StreamTopology;
import stream.runtime.setup.ObjectFactory;
import backtype.storm.topology.TopologyBuilder;

/**
 * @author chris
 * 
 */
public class QueueHandler extends ATopologyElementHandler {

	/**
	 * @param of
	 */
	public QueueHandler(ObjectFactory of) {
		super(of);
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

	}
}
