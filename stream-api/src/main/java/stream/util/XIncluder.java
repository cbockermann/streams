/**
 * 
 */
package stream.util;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import stream.io.SourceURL;

/**
 * 
 * 
 * @author Christian Bockermann
 * 
 */
public class XIncluder {

	static Logger log = LoggerFactory.getLogger(XIncluder.class);

	public Document perform(Document doc) throws Exception {
		return perform(doc, new Variables());
	}

	public Document perform(Document doc, Variables context) throws Exception {
		return perform(doc, context, false);
	}

	public Document perform(Document doc, Variables context,
			boolean ignoreProperties) throws Exception {

		Variables vars = null;
		if (ignoreProperties)
			vars = new Variables(context);
		else
			vars = handleProperties(doc, context);

		// vars.expandAndAdd(context);
		NodeList includes = doc.getElementsByTagName("include");

		// Since we remove the elements from the node directly later on, we must
		// not iterate over the NodeList object but over a copy.
		final List<Element> includeElements = new ArrayList<Element>();
		for (int i = 0; i < includes.getLength(); i++) {
			includeElements.add((Element) includes.item(i));
		}
		for (Element include : includeElements) {

			Document included = null;

			String file = include.getAttribute("file");
			String url = include.getAttribute("url");

			// New Context
			String includeId = include.getAttribute("id");
			includeId = context.expand(includeId, true);

			String includeCopies = include.getAttribute("copies");
			includeCopies = context.expand(includeCopies, true);

			Variables includeProperties = new Variables(vars);

			if (!includeId.isEmpty()) {
				includeProperties.put("include.id", includeId);
			}

			if (!includeCopies.isEmpty()) {
				includeProperties.put("include.copies", includeId);
			}

			if (url != null && !url.trim().isEmpty()) {
				log.debug("Found xinclude for URL {}", url);

				url = includeProperties.expand(url);
				log.info("   url expanded to: '{}'", url);
				SourceURL source = new SourceURL(url);
				log.debug("reading document from {}", source);
				included = XMLUtils.parseDocument(source.openStream());
				String tmpIncluded = XMLUtils.toString(included);
				tmpIncluded = tmpIncluded.replace("${include.id}", includeId);
				tmpIncluded = tmpIncluded.replace("${include.copies}",
						includeCopies);
				included = XMLUtils.parseDocument(tmpIncluded);
				file = null;
			}

			if (file != null) {
				log.debug("including file '{}'", file);
				file = vars.expand(file);
				log.debug("   file expanded to '{}'", file);

				File f = new File(file);
				if (f.canRead()) {
					log.debug("including document from {}", f.getAbsolutePath());
					included = XMLUtils.parseDocument(f);
					String tmpIncluded = XMLUtils.toString(included);
					tmpIncluded.replace("${include.id}", includeId);
					tmpIncluded.replace("${include.copies}", includeCopies);
					included = XMLUtils.parseDocument(tmpIncluded);
				} else {
					log.debug("No file found for {}, checking classpath...",
							file);
					if (!file.startsWith("/"))
						file = "/" + file;
					URL rurl = XIncluder.class.getResource(file);
					log.debug("   found resource {} instead!", rurl);
					if (rurl != null) {
						included = XMLUtils.parseDocument(rurl.openStream());
						String tmpIncluded = XMLUtils.toString(included);
						tmpIncluded.replace("${include.id}", includeId);
						tmpIncluded.replace("${include.copies}", includeCopies);
						included = XMLUtils.parseDocument(tmpIncluded);
					}
				}
			}

			if (included == null) {
				log.error(
						"Failed to include document for include tag with attributes {}",
						XMLUtils.getAttributes(include));
				continue;
			} else {
				log.debug("Recursively including documents... ");
				XIncluder nested = new XIncluder();
				included = nested.perform(included, includeProperties, true);
			}

			Element parent = (Element) include.getParentNode();

			final ArrayList<Element> newElements = new ArrayList<Element>();

			NodeList includeList = included.getDocumentElement()
					.getChildNodes();

			if (includeList.getLength() == 0) {
				//
				// if the root-element of the included document does not have
				// any child-nodes, we need to include it directly
				//
				Element subTree = (Element) doc.importNode(
						included.getDocumentElement(), true);
				parent.replaceChild(subTree, include);

			} else {
				//
				// otherwise, the root-element is just regarded as a wrapper
				// around the elements to be included and we skip this
				// wrapper-layer
				//
				for (int j = 0; j < includeList.getLength(); j++) {
					Node node = includeList.item(j);
					if (node.getNodeType() == Node.ELEMENT_NODE) {
						Element newElem = (Element) doc.importNode(node, true);
						newElements.add(newElem);
					}
				}

				for (Element el : newElements) {
					parent.insertBefore(el, include);
				}
				parent.removeChild(include);
			}
		}

		return doc;
	}

	private Variables handleProperties(Document doc, Variables variables) {
		PropertiesHandler pHandle = new PropertiesHandler();

		Variables systemVariables = new Variables();
		pHandle.addSystemProperties(systemVariables);

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
			pHandle.handlePropertyElement(prop, variables, systemVariables);
		}
		// find
		list = doc.getElementsByTagName("Property");
		// handle
		for (int i = 0; i < list.getLength(); i++) {
			Element prop = (Element) list.item(i);
			pHandle.handlePropertyElement(prop, variables, systemVariables);
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
			pHandle.handlePropertiesElement(e, variables, systemVariables);
		}
		// find
		list = doc.getElementsByTagName("Properties");
		// handle
		for (int i = 0; i < list.getLength(); i++) {
			Element e = (Element) list.item(i);
			pHandle.handlePropertiesElement(e, variables, systemVariables);
		}

		// add system properties, e.g defined at command line using the -D flag:
		// java -Dproperty-name=property-value
		//
		pHandle.addSystemProperties(variables);

		// process-local properties at processElementHandler

		return variables;
	}
}