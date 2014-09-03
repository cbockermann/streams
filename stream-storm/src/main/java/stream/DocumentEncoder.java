/*
 *  streams library
 *
 *  Copyright (C) 2011-2014 by Christian Bockermann, Hendrik Blom
 * 
 *  streams is a library, API and runtime environment for processing high
 *  volume data streams. It is composed of three submodules "stream-api",
 *  "stream-core" and "stream-runtime".
 *
 *  The streams library (and its submodules) is free software: you can 
 *  redistribute it and/or modify it under the terms of the 
 *  GNU Affero General Public License as published by the Free Software 
 *  Foundation, either version 3 of the License, or (at your option) any 
 *  later version.
 *
 *  The stream.ai library (and its submodules) is distributed in the hope
 *  that it will be useful, but WITHOUT ANY WARRANTY; without even the implied 
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/.
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
 * This class provides methods for decoding/encoding an XML document into a
 * string, a base-64 encoded string and vice-versa.
 * 
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public class DocumentEncoder {

	/**
	 * Encode a DOM document into a base-64 encoded string.
	 * 
	 * @param doc
	 * @return
	 * @throws Exception
	 */
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

	/**
	 * Decode a base-64 encoded XML string into a DOM document.
	 * 
	 * @param xml
	 * @return
	 * @throws Exception
	 */
	public static Document decodeDocument(String xml) throws Exception {
		Base64 b64 = new Base64();
		byte[] data = b64.decode(xml.getBytes());
		DocumentBuilder builder = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();
		Document doc = builder.parse(new ByteArrayInputStream(data));
		return doc;
	}

	/**
	 * Encode the given document (DOM) into an XML string representation.
	 * 
	 * @param doc
	 * @return
	 * @throws Exception
	 */
	public static String toXMLString(Document doc) throws Exception {
		StringWriter out = new StringWriter();

		Transformer transform = TransformerFactory.newInstance()
				.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(out);
		transform.transform(source, result);

		out.close();
		return out.toString();
	}
}
