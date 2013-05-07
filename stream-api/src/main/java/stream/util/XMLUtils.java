/**
 * 
 */
package stream.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <p>
 * This class provides several utility methods for pre-processing an XML stream
 * definition document.
 * </p>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public class XMLUtils {

	public final static String UUID_ATTRIBUTE = "stream.storm.uuid";

	public static Document parseDocument(String xmlString) throws Exception {
		//
		// TODO: Enhance this!
		//
		return parseDocument(new ByteArrayInputStream(xmlString.getBytes()));
	}

	public static Document parseDocument(File file) throws Exception {
		return parseDocument(new FileInputStream(file));
	}

	public static Document parseDocument(InputStream in) throws Exception {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();
		return builder.parse(in);
	}

	public static String toString(Document doc) throws Exception {

		Transformer trans = TransformerFactory.newInstance().newTransformer();
		Source source = new DOMSource(doc);
		StringWriter out = new StringWriter();
		Result output = new StreamResult(out);
		trans.transform(source, output);

		String xml = out.toString();
		return xml;

	}

	/**
	 * Add random UUIDs to all elements of a document.
	 * 
	 * @param doc
	 * @param attributeName
	 *            The name of the attribute which will hold the UUID.
	 * @return
	 */
	public static Document addUUIDAttributes(Document doc, String attributeName) {
		if (doc != null && doc.getDocumentElement() != null)
			addUUIDAttributes(doc.getDocumentElement(), attributeName);
		return doc;
	}

	/**
	 * This method generates a new random UUID and attaches it in a new
	 * attribute to the element.
	 * 
	 * @param element
	 *            The element to attach the new UUID to.
	 * @param attributeName
	 *            The name of the attribute which will hold the UUID.
	 */
	public static void addUUIDAttributes(Element element, String attributeName) {

		if (element.hasAttribute(UUID_ATTRIBUTE)) {
			return;
		}

		String id = element.getAttribute("id");
		String uuid = UUID.randomUUID().toString().toUpperCase();
		if (id != null && !id.trim().isEmpty()) {
			uuid = id;
		} else {
			id = uuid;
			System.err.println("Replacing ID with " + uuid);
			element.setAttribute("id", id);
		}

		element.setAttribute(UUID_ATTRIBUTE, uuid);

		NodeList list = element.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				addUUIDAttributes((Element) node, attributeName);
			}
		}
	}

	/**
	 * This method parses an XML document from an input-stream and adds random
	 * UUIDs to all elements of that document. The resulting XML is returned as
	 * a string.
	 * 
	 * @param in
	 *            The input stream to read the XML document from.
	 * @param attributeName
	 *            The name of the attribute which will hold the UUID.
	 * @return The XML string with elements extended by UUIDs.
	 * @throws Exception
	 */
	public static String createIDs(InputStream in, String attributeName)
			throws Exception {

		DocumentBuilder builder = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();
		Document doc = builder.parse(in);

		addUUIDAttributes(doc.getDocumentElement(), attributeName);

		Transformer trans = TransformerFactory.newInstance().newTransformer();
		Source source = new StreamSource(in);
		StringWriter out = new StringWriter();
		Result output = new StreamResult(out);
		trans.transform(source, output);

		String xml = out.toString();
		return xml;
	}

	/**
	 * This method return an element referenced by a unique identifier. If no
	 * such element exists, this method will return <code>null</code>.
	 * 
	 * @param el
	 * @param uuid
	 * @return
	 */
	public static Element findElementByUUID(Element el, String attributeName,
			String uuid) {
		String id = el.getAttribute(attributeName);
		if (uuid.equals(id)) {
			return el;
		}

		NodeList list = el.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {

				Element found = findElementByUUID((Element) node,
						attributeName, uuid);
				if (found != null)
					return found;
			}
		}

		return null;
	}

	public static Element findElementByUUID(Document doc, String attributeName,
			String uuid) {
		if (doc == null)
			return null;
		return findElementByUUID(doc.getDocumentElement(), attributeName, uuid);
	}

	public static Document createDocument(Element element, String docElementName)
			throws Exception {

		DocumentBuilder builder = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();
		Document doc = builder.newDocument();
		Element root = doc.createElement(docElementName);

		doc.appendChild(root);
		root.appendChild(doc.adoptNode(element.cloneNode(true)));

		return doc;
	}

	public static String createDocumentXML(Element element,
			String docElementName) throws Exception {
		Document doc = createDocument(element, docElementName);
		Transformer xform = TransformerFactory.newInstance().newTransformer();
		xform.setOutputProperty(OutputKeys.INDENT, "yes");
		xform.setOutputProperty("{http://xml.apache.org/xslt}indent-amount",
				"2");
		StringWriter writer = new StringWriter();
		xform.transform(new DOMSource(doc), new StreamResult(writer));
		writer.close();
		return writer.toString();
	}

	public static Map<String, String> getAttributes(Node node) {
		Map<String, String> map = new LinkedHashMap<String, String>();
		NamedNodeMap att = node.getAttributes();
		for (int i = 0; i < att.getLength(); i++) {
			Node attr = att.item(i);
			String value = attr.getNodeValue();
			map.put(attr.getNodeName(), value);
		}
		return map;
	}

	public static String extract(String[] elems, Document doc) {
		return extract(elems, 0, doc.getDocumentElement());
	}

	public static String extract(String[] path, int level, Element el) {
		if (level == path.length - 1)
			return el.getTextContent();

		NodeList list = el.getElementsByTagName(path[level]);
		if (list.getLength() == 0)
			return null;

		Element e = (Element) list.item(0);
		return extract(path, level + 1, e);
	}
}