/**
 * 
 */
package stream.util;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import stream.Data;

/**
 * @author chris
 * 
 */
public class XMLUtilsTest {

	static Logger log = LoggerFactory.getLogger(XMLUtilsTest.class);
	String XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><result><id>123</id></result>";

	@Test
	public void test() {

		try {
			Document doc = XMLUtils.parseDocument(XML);
			Data item = XMLUtils.parseIntoDataItem(doc,
					new String[] { "result" });
			log.info("Parsed into item: {}", item);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Test failed: " + e.getMessage());
		}
	}
}
