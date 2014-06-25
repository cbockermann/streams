package stream.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import stream.io.SourceURL;

public class PropertiesHandler {

	private Logger log = LoggerFactory.getLogger(PropertiesHandler.class);

	/**
	 * This method adds properties defined in the format as provided by maven's
	 * <code>&lt;properties&gt;...&lt;/properties&gt;</code> element.
	 * 
	 * @param container
	 * @param doc
	 */
	public void handlePropertiesElement(Element prop, Variables variables,
			Variables systemProperties) {

		String suffix = "";
		if (prop.hasAttribute("suffix")) {
			suffix = prop.getAttribute("suffix");
			log.info("Add suffix {} to properties.", suffix);
		}

		NodeList children = prop.getChildNodes();
		if (children.getLength() > 0) {
			// TextNodes
			for (int k = 0; k < children.getLength(); k++) {

				Node ch = children.item(k);
				if (ch.getNodeType() == Node.ELEMENT_NODE) {

					String key = ch.getNodeName();
					String value = ch.getTextContent();

					variables.set(key + suffix, value);
				}
			}

		}
		// Properties from URL
		else if (prop.hasAttribute("url")) {
			String purl = prop.getAttribute("url");
			log.debug("Reading properties from URL {}", purl);
			try {
				// ORDER IMPORTANT
				Variables props = new Variables(variables);
				props.addVariables(systemProperties);
				purl = props.expand(purl);
				log.debug("Properties URL is: {}", purl);

				SourceURL propUrl = new SourceURL(purl);

				Properties p = new Properties();
				p.load(propUrl.openStream());
				for (Object k : p.keySet()) {
					String key = k.toString();
					String value = p.getProperty(key);
					value = systemProperties.expand(value);
					log.debug("Adding property '{}' = '{}'", key, value);
					variables.set(key + suffix, value);
				}

			} catch (Exception e) {
				log.error("Failed to read properties from url {}: {}", purl,
						e.getMessage());
			}
			// Properties from URL
		} else if (prop.hasAttribute("file")) {
			File file = new File(prop.getAttribute("file"));
			try {
				Properties p = new Properties();
				p.load(new FileInputStream(file));
				for (Object k : p.keySet()) {
					variables.set(k.toString() + suffix,
							p.getProperty(k.toString()));
				}
			} catch (Exception e) {
				log.error("Failed to read properties from file {}: {}", file,
						e.getMessage());
			}
		}

	}

	public void handlePropertyElement(Element prop, Variables variables,
			Variables systemVariables) {
		if (prop.getNodeName().equalsIgnoreCase("property")) {

			String key = prop.getAttribute("name");
			String value = prop.getAttribute("value");

			if (key != null && !"".equals(key.trim()) && value != null
					&& !"".equals(value.trim())) {
				// ORDER IMPORTANT
				Variables props = new Variables(variables);
				props.addVariables(systemVariables);
				String k = key.trim();
				String v = value.trim();
				// ORDER IMPORTANT
				// // All found variables ()
				v = props.expand(v);
				// log.info("Setting property {} = {}", k, v);
				variables.set(k, v);
			}
		}
	}

	/**
	 * This method adds all the system properties to the container properties,
	 * possibly overwriting pre-defined properties.
	 * 
	 * @param container
	 */
	public void addSystemProperties(Variables variables) {
		for (Object key : System.getProperties().keySet()) {
			variables.set(key.toString(), System.getProperty(key.toString()));
		}
	}

}
