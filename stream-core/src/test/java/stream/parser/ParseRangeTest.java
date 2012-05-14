/**
 * 
 */
package stream.parser;

import java.util.Map;

import org.junit.Test;

import stream.util.parser.Parser;
import stream.util.parser.ParserGenerator;

/**
 * @author chris
 * 
 */
public class ParseRangeTest {

	@Test
	public void test() throws Exception {

		String grammar = "[%(MIN),%(MAX)]";

		ParserGenerator pg = new ParserGenerator(grammar);
		Parser<Map<String, String>> parser = pg.newParser();

		String input = "[5.1738,13.90834534]";
		Map<String, String> result = parser.parse(input);

		for (String key : result.keySet()) {
			System.out.println(key + " => " + result.get(key));
		}

		// fail("Not yet implemented");
	}
}
