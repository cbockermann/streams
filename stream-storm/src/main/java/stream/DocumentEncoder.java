/**
 * 
 */
package stream;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Document;

/**
 * @author chris
 * 
 */
public class DocumentEncoder {

	public static String encodeDocument(Document doc) throws Exception {

		StringWriter out = new StringWriter();

		Transformer transform = TransformerFactory.newInstance()
				.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(out);
		transform.transform(source, result);

		Base64 b64 = new Base64();
		byte[] encoded = b64.encode(out.toString().getBytes());
		return (new String(encoded)).replaceAll("\r\n", "");
	}

	public static Document decodeDocument(String xml) throws Exception {
		Base64 b64 = new Base64();
		byte[] data = b64.decode(xml.getBytes());
		DocumentBuilder builder = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();
		Document doc = builder.parse(new ByteArrayInputStream(data));
		return doc;
	}
}
