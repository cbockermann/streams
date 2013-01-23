/**
 * 
 */
package stream.storm.config;

import org.w3c.dom.Element;

import stream.StreamTopology;
import backtype.storm.topology.TopologyBuilder;

/**
 * @author chris
 * 
 */
public interface ConfigHandler {

	public boolean handles(Element el);

	public void handle(Element element, StreamTopology st,
			TopologyBuilder builder) throws Exception;
}