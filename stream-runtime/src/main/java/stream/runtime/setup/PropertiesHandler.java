/**
 * 
 */
package stream.runtime.setup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import stream.runtime.ProcessContainer;
import stream.runtime.VariableContext;

/**
 * <p>
 * This handler extracts all properties defined in a document and adds these to
 * the process container.
 * </p>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public class PropertiesHandler extends VariableContext implements
		DocumentHandler {

	static Logger log = LoggerFactory.getLogger(PropertiesHandler.class);

	/**
	 * @see stream.runtime.setup.DocumentHandler#handle(stream.runtime.ProcessContainer,
	 *      org.w3c.dom.Document)
	 */
	@Override
	public void handle(ProcessContainer container, Document doc)
			throws Exception {

		// handle maven-like properties, e.g.
		// <properties>
		// <property-name>value-of-property</property-name>
		// </properties>
		//
		findPropertiesElements(container, doc);

		// handle property elements, i.e.
		// <property>
		// <name>property-name</name>
		// <value>property-value</value>
		// </property>
		//
		findPropertyElements(container, doc);

		// add system properties, e.g defined at command line using the -D flag:
		// java -Dproperty-name=property-value
		//
		addSystemProperties(container);
	}

	/**
	 * This method finds and adds properties defined in the format as provided
	 * by maven's <code>&lt;properties&gt;...&lt;/properties&gt;</code> element.
	 * 
	 * @param container
	 * @param doc
	 */
	private void findPropertiesElements(ProcessContainer container, Document doc) {
		NodeList list = doc.getElementsByTagName("properties");
		for (int i = 0; i < list.getLength(); i++) {

			Element prop = (Element) list.item(i);

			NodeList children = prop.getChildNodes();
			for (int k = 0; k < children.getLength(); k++) {

				Node ch = children.item(k);
				if (ch.getNodeType() == Node.ELEMENT_NODE) {

					String key = ch.getNodeName();
					String value = ch.getTextContent();

					container.getContext().setProperty(key, value);
					set(key, value);
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
	private void findPropertyElements(ProcessContainer container, Document doc) {

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
						log.info("Setting property {} = {}", k, v);
						container.getContext().setProperty(k, v);
						set(k, v);
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
	private void addSystemProperties(ProcessContainer container) {
		for (Object key : System.getProperties().keySet()) {
			container.getContext().setProperty(key.toString(),
					System.getProperty(key.toString()));
			set(key.toString(), System.getProperty(key.toString()));
		}
	}
}
