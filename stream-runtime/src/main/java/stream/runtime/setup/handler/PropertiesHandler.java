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

	private stream.util.PropertiesHandler pHandle;

	public PropertiesHandler() {
		pHandle = new stream.util.PropertiesHandler();
	}

	/**
	 * 
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
		pHandle.addSystemProperties(systemVariables);

		// // Add variables to systemVariables to have original state (Not
		// Needed)
		// systemVariables.addVariables(variables);

		// handle maven-like properties, e.g.
		// <properties>
		// <property-name>value-of-property</property-name>
		// </properties>
		// and <properties url="${urlToProperties}"

		NodeList list = doc.getElementsByTagName("properties");
		// handle
		for (int i = 0; i < list.getLength(); i++) {
			Element e = (Element) list.item(i);
			pHandle.handlePropertiesElement(e, variables, systemVariables);
		}
		// find
		list = doc.getElementsByTagName("Properties");
		// handle
		for (int i = 0; i < list.getLength(); i++) {
			Element e = (Element) list.item(i);
			pHandle.handlePropertiesElement(e, variables, systemVariables);
		}

		// handle property elements, i.e.
		// <property>
		// <name>property-name</name>
		// <value>property-value</value>
		// </property>
		//
		list = doc.getElementsByTagName("property");
		// handle
		for (int i = 0; i < list.getLength(); i++) {
			Element prop = (Element) list.item(i);
			pHandle.handlePropertyElement(prop, variables, systemVariables);
		}
		// find
		list = doc.getElementsByTagName("Property");
		// handle
		for (int i = 0; i < list.getLength(); i++) {
			Element prop = (Element) list.item(i);
			pHandle.handlePropertyElement(prop, variables, systemVariables);
		}

		// add system properties, e.g defined at command line using the -D flag:
		// java -Dproperty-name=property-value
		//
		pHandle.addSystemProperties(variables);

		// process-local properties at processElementHandler
	}

}
