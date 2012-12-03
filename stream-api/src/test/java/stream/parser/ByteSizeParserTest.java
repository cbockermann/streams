/**
 * 
 */
package stream.parser;

import junit.framework.Assert;

import org.junit.Test;

import stream.util.parser.ByteSizeParser;

/**
 * @author chris
 * 
 */
public class ByteSizeParserTest {

	@Test
	public void test() {
		test("1M", 1024 * 1024L);
		test("1m", 1024 * 1024L);
		test("1mb", 1024 * 1024L);
	}

	@Test
	public void test2() {
		test("1536", 1536L);
		test("1536K", (1536) * 1024L);
	}

	protected void test(String input, long expBytes) {
		try {

			Long bytes = ByteSizeParser.parseByteSize(input);
			Assert.assertEquals(expBytes, bytes.longValue());

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}
}
