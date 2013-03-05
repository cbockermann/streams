/**
 * 
 */
package stream.io;

import static org.junit.Assert.fail;
import junit.framework.Assert;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;

/**
 * @author chris
 * 
 */
public class CsvStreamTest {

	static Logger log = LoggerFactory.getLogger(CsvStreamTest.class);

	@Test
	public void testWithHeader() {

		try {
			SourceURL src = new SourceURL("classpath:/stream1.csv");
			CsvStream stream = new CsvStream(src);
			stream.init();

			Data item = stream.read();
			log.info("item: {}", item);

			Assert.assertTrue(item.containsKey("x")
					&& item.containsKey("stream"));

			stream.close();

		} catch (Exception e) {
			fail("Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Test
	public void testKeys() {

		try {
			SourceURL src = new SourceURL("classpath:/stream1.csv");
			CsvStream stream = new CsvStream(src);
			stream.setHeader(false);
			stream.setKeys("x,stream".split(","));
			stream.init();

			Data item = stream.read();
			log.info("item: {}", item);

			Assert.assertTrue(item.containsKey("x")
					&& item.containsKey("stream"));

			stream.close();

		} catch (Exception e) {
			fail("Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Test
	public void testKeysSkipComments() {

		try {
			SourceURL src = new SourceURL("classpath:/stream2.csv");
			CsvStream stream = new CsvStream(src);
			stream.init();

			Data item = stream.read();
			log.info("item: {}", item);

			Assert.assertFalse(item.containsKey("x")
					&& item.containsKey("stream"));

			stream.close();

		} catch (Exception e) {
			fail("Error: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
