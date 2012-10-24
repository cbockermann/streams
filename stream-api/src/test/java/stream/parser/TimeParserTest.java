/**
 * 
 */
package stream.parser;

import junit.framework.Assert;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.util.parser.TimeParser;

/**
 * @author chris
 * 
 */
public class TimeParserTest {

	static Logger log = LoggerFactory.getLogger(TimeParserTest.class);

	/**
	 * Test method for
	 * {@link stream.util.parser.TimeParser#parseTime(java.lang.String)}.
	 */
	@Test
	public void testParseTime() {
		String in = "10ms";
		try {
			Long time = TimeParser.parseTime(in);
			System.out.println("'" + in + "' parses to milliseconds: " + time);
			Assert.assertEquals(1000 * 10L, time.longValue());
		} catch (Exception e) {
			Assert.fail("Test failed: " + e.getMessage());
		}
	}
}
