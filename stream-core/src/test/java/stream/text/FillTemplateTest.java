/**
 * 
 */
package stream.text;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.data.DataFactory;
import stream.util.URLUtilities;

/**
 * @author chris
 * 
 */
public class FillTemplateTest {

	static Logger log = LoggerFactory.getLogger(FillTemplateTest.class);

	@Test
	public void test() {

		try {
			String text = URLUtilities.readContent(FillTemplateTest.class
					.getResource("/test.txt"));

			Data item = DataFactory.create();
			item.put("name", "chris");
			// item.put("@id", "1");
			item.put("@timestamp", System.currentTimeMillis());
			item.put("text", text);

			FillTemplate filler = new FillTemplate();
			filler.setKey("text");
			filler.setEmptyStrings(false);

			item = filler.process(item);

			log.info("text:\n{}", item.get("text"));

		} catch (Exception e) {
			fail("Error: " + e.getMessage());
		}
	}
}