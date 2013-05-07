/**
 * 
 */
package stream.util;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

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

		NodeList includes = doc.getElementsByTagName("include");
		for (int i = 0; i < includes.getLength(); i++) {

			Element include = (Element) includes.item(i);
			Document included = null;

			String file = include.getAttribute("file");
			String url = include.getAttribute("url");
			if (url != null && !url.trim().isEmpty()) {
				log.debug("Found xinclude for URL {}", url);
				SourceURL source = new SourceURL(url);
				log.debug("reading document from {}", source);
				included = XMLUtils.parseDocument(source.openStream());
				file = null;
			}

			if (file != null) {
				File f = new File(file);
				if (f.canRead()) {
					log.debug("including document from {}", f.getAbsolutePath());
					included = XMLUtils.parseDocument(f);
				} else {
					log.debug("No file found for {}, checking classpath...",
							file);
					if (!file.startsWith("/"))
						file = "/" + file;
					URL rurl = XIncluder.class.getResource(file);
					log.debug("   found resource {} instead!", rurl);
					if (rurl != null) {
						included = XMLUtils.parseDocument(rurl.openStream());
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
				included = nested.perform(included);
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
}