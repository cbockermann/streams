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
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
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

		// add system properties, e.g defined at command line using the -D flag:
		// java -Dproperty-name=property-value
		//
		Variables systemVariables = new Variables();
		addSystemProperties(systemVariables);
		systemVariables.addVariables(variables);
		// handle maven-like properties, e.g.
		// <properties>
		// <property-name>value-of-property</property-name>
		// </properties>
		//
		findPropertiesElements(container, doc, variables, systemVariables);

		// handle property elements, i.e.
		// <property>
		// <name>property-name</name>
		// <value>property-value</value>
		// </property>
		//
		findPropertyElements(container, doc, variables);

		// add system properties, e.g defined at command line using the -D flag:
		// java -Dproperty-name=property-value
		//
		addSystemProperties(variables);

	}

	/**
	 * This method finds and adds properties defined in the format as provided
	 * by maven's <code>&lt;properties&gt;...&lt;/properties&gt;</code> element.
	 * 
	 * @param container
	 * @param doc
	 */
	private void findPropertiesElements(IContainer container, Document doc,
			Variables variables, Variables systemProperties) {
		NodeList list = doc.getElementsByTagName("properties");
		for (int i = 0; i < list.getLength(); i++) {

			Element prop = (Element) list.item(i);

			NodeList children = prop.getChildNodes();
			for (int k = 0; k < children.getLength(); k++) {

				Node ch = children.item(k);
				if (ch.getNodeType() == Node.ELEMENT_NODE) {

					String key = ch.getNodeName();
					String value = ch.getTextContent();

					variables.set(key, value);
				}
			}

			if (prop.hasAttribute("url")) {
				String purl = prop.getAttribute("url");
				// url via systemProperties;
				purl = systemProperties.expand(purl);
				try {
					SourceURL propUrl = new SourceURL(purl);

					Properties p = new Properties();
					p.load(propUrl.openStream());
					for (Object k : p.keySet()) {
						variables
								.set(k.toString(), p.getProperty(k.toString()));
					}

				} catch (Exception e) {
					log.error("Failed to read properties from url {}: {}",
							purl, e.getMessage());
				}
			}
			if (prop.hasAttribute("file")) {
				File file = new File(prop.getAttribute("file"));
				try {
					Properties p = new Properties();
					p.load(new FileInputStream(file));
					for (Object k : p.keySet()) {
						variables
								.set(k.toString(), p.getProperty(k.toString()));
					}
				} catch (Exception e) {
					log.error("Failed to read properties from file {}: {}",
							file, e.getMessage());
				}
			}
		}
	}

	/**
	 * Check for property elements in the document and add all the defined
	 * properties to the container.
	 * 
	 * @param container
	 * @param doc
	 */
	private void findPropertyElements(IContainer container, Document doc,
			Variables variables) {

		NodeList ch = doc.getElementsByTagName("property");
		for (int i = 0; i < ch.getLength(); i++) {
			Node child = ch.item(i);
			if (child instanceof Element) {
				Element el = (Element) child;
				if (el.getNodeName().equalsIgnoreCase("property")) {

					String key = el.getAttribute("name");
					String value = el.getAttribute("value");

					if (key != null && !"".equals(key.trim()) && value != null
							&& !"".equals(value.trim())) {
						String k = key.trim();
						String v = value.trim();
						// log.info("Setting property {} = {}", k, v);
						variables.set(k, v);
					}
				}
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
