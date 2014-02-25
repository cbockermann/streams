/**
 * 
 */
package stream.runtime.setup.handler;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import stream.io.SourceURL;
import stream.runtime.DependencyInjection;
import stream.runtime.IContainer;
import stream.util.Variables;

/**
 * <p>
 * This handler extracts all properties defined in a document and adds these to
 * the process container.
 * </p>
 * 
 * @author Christian Bockermann, Hendrik Blom
 * 
 */
public class PropertiesHandler implements DocumentHandler {

	static Logger log = LoggerFactory.getLogger(PropertiesHandler.class);

	/**
	 * @see stream.runtime.setup.handler.DocumentHandler#handle(stream.runtime.ProcessContainer,
	 *      org.w3c.dom.Document)
	 */
	@Override
	public void handle(IContainer container, Document doc, Variables variables,
			DependencyInjection depInj) throws Exception {

		// ${Home}/streams.properties already added.(streams.run())

		// Read system properties, e.g defined at command line using the -D
		// flag:
		// java -Dproperty-name=property-value
		//
		Variables systemVariables = new Variables();
		addSystemProperties(systemVariables);

		// // Add variables to systemVariables to have original state (Not
		// Needed)
		// systemVariables.addVariables(variables);

		// handle property elements, i.e.
		// <property>
		// <name>property-name</name>
		// <value>property-value</value>
		// </property>
		//
		// find
		NodeList list = doc.getElementsByTagName("property");
		// handle
		for (int i = 0; i < list.getLength(); i++) {
			Element prop = (Element) list.item(i);
			handlePropertyElement(prop, variables, systemVariables);
		}
		// find
		list = doc.getElementsByTagName("Property");
		// handle
		for (int i = 0; i < list.getLength(); i++) {
			Element prop = (Element) list.item(i);
			handlePropertyElement(prop, variables, systemVariables);
		}
		// handle maven-like properties, e.g.
		// <properties>
		// <property-name>value-of-property</property-name>
		// </properties>
		// and <properties url="${urlToProperties}"

		// find
		list = doc.getElementsByTagName("properties");
		// handle
		for (int i = 0; i < list.getLength(); i++) {
			Element e = (Element) list.item(i);
			handlePropertiesElement(e, variables, systemVariables);
		}
		// find
		list = doc.getElementsByTagName("Properties");
		// handle
		for (int i = 0; i < list.getLength(); i++) {
			Element e = (Element) list.item(i);
			handlePropertiesElement(e, variables, systemVariables);
		}

		// add system properties, e.g defined at command line using the -D flag:
		// java -Dproperty-name=property-value
		//
		addSystemProperties(variables);

		// process-local properties at processElementHandler
	}

	/**
	 * This method adds properties defined in the format as provided by maven's
	 * <code>&lt;properties&gt;...&lt;/properties&gt;</code> element.
	 * 
	 * @param container
	 * @param doc
	 */
	private void handlePropertiesElement(Element prop, Variables variables,
			Variables systemProperties) {
		NodeList children = prop.getChildNodes();
		if (children.getLength() > 0) {
			// TextNodes
			for (int k = 0; k < children.getLength(); k++) {

				Node ch = children.item(k);
				if (ch.getNodeType() == Node.ELEMENT_NODE) {

					String key = ch.getNodeName();
					String value = ch.getTextContent();

					variables.set(key, value);
				}
			}

		}
		// Properties from URL
		else if (prop.hasAttribute("url")) {
			String purl = prop.getAttribute("url");
			try {
				// ORDER IMPORTANT
				Variables props = new Variables(variables);
				props.addVariables(systemProperties);
				purl = props.expand(purl);

				SourceURL propUrl = new SourceURL(purl);

				Properties p = new Properties();
				p.load(propUrl.openStream());
				for (Object k : p.keySet()) {
					variables.set(k.toString(), p.getProperty(k.toString()));
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
					variables.set(k.toString(), p.getProperty(k.toString()));
				}
			} catch (Exception e) {
				log.error("Failed to read properties from file {}: {}", file,
						e.getMessage());
			}
		}

	}

	private void handlePropertyElement(Element prop, Variables variables,
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
	private void addSystemProperties(Variables variables) {
		for (Object key : System.getProperties().keySet()) {
			variables.set(key.toString(), System.getProperty(key.toString()));
		}
	}

}
