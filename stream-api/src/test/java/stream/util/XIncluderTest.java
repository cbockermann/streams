/**
 * 
 */
package stream.util;

import static org.junit.Assert.fail;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import stream.io.SourceURL;

/**
 * @author chris
 * 
 */
public class XIncluderTest {

	static Logger log = LoggerFactory.getLogger(XIncluderTest.class);

	@Test
	public void test() {

		try {
			SourceURL url = new SourceURL("classpath:/include.xml");
			log.info("Expanding inclusions in file {}", url);
			Document doc = XMLUtils.parseDocument(url.openStream());
			XIncluder includer = new XIncluder();
			doc = includer.perform(doc);

			Transformer transformer = TransformerFactory.newInstance()
					.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(
					"{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.transform(new DOMSource(doc), new StreamResult(
					System.out));

		} catch (Exception e) {
			e.printStackTrace();
			fail("Test failed: " + e.getMessage());
		}
	}
}
