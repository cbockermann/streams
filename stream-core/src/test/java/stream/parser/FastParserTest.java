/**
 * 
 */
package stream.parser;

import static org.junit.Assert.fail;

import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.util.parser.GenericParser;
import stream.util.parser.GenericTurboParser;
import stream.util.parser.Parser;
import stream.util.parser.ParserGenerator;
import stream.util.parser.ParserGenerator.Token;

/**
 * @author chris
 * 
 */
public class FastParserTest {

	static Logger log = LoggerFactory.getLogger(FastParserTest.class);
	public final static String INPUT = "84.190.232.52 - - [20/May/2011:22:57:57 +0200] \"GET / HTTP/1.1\" 200 2950 \"-\" \"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_6_7) AppleWebKit/534.24 (KHTML, like Gecko) Chrome/11.0.696.68 Safari/534.24\"";
	public final static String format = "%(REMOTE_ADDR) %(HOST) %(REMOTE_USER) [%(DAY)/%(MONTH)/%(YEAR):%(TIME)] \"%(METHOD) %(URI) %(PROTOCOL)\" %(STATUS) %(SIZE) \"%(d)\" \"%(USER_AGENT)\"";

	Parser<Map<String, String>> parser;
	Parser<Map<String, String>> turboParser;

	@Before
	public void setup() {
		List<Token> tokens = ParserGenerator.readGrammar(format);
		parser = new GenericParser(tokens);
		turboParser = new GenericTurboParser(tokens);
	}

	@Test
	public void test() {

		try {
			Map<String, String> out1 = parser.parse(INPUT);
			Map<String, String> out2 = turboParser.parse(INPUT);

			Assert.assertNotNull(out1);
			Assert.assertNotNull(out2);

			Assert.assertEquals(out1.size(), out2.size());

			for (String k : out1.keySet()) {
				Assert.assertTrue(out2.containsKey(k));
				Assert.assertEquals(out1.get(k), out2.get(k));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testSpeed() {

		Integer rounds = 100000;

		try {
			Long start = System.currentTimeMillis();
			for (int r = 0; r < rounds; r++) {
				parser.parse(INPUT);
			}

			Long end = System.currentTimeMillis();
			log.info("Processed {} items in {} ms", rounds, (end - start));
			log.info("Rate is {}/second", rounds / ((end - start) / 1000.0d));

		} catch (Exception e) {
			e.printStackTrace();
			fail("Test failed: " + e.getMessage());
		}
	}
}
