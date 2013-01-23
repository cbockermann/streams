/**
 * 
 */
package stream.storm.config;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import stream.runtime.setup.ObjectFactory;

/**
 * @author chris
 * 
 */
public abstract class ATopologyElementHandler implements ConfigHandler {

	protected final ObjectFactory objectFactory;

	public ATopologyElementHandler(ObjectFactory of) {
		this.objectFactory = of;
	}

	protected List<String> getInputNames(Element el) {
		List<String> inputs = new ArrayList<String>();
		String input = el.getAttribute("input");
		if (input == null)
			return inputs;

		if (input.indexOf(",") < 0) {
			inputs.add(input.trim());
			return inputs;
		}

		for (String in : input.split(",")) {
			if (in != null && !in.trim().isEmpty()) {
				inputs.add(in.trim());
			}
		}
		return inputs;
	}
}
