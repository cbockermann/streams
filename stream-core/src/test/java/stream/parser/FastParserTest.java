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
import stream.util.parser.Token;

/**
 * @author chris
 * 
 */
public class FastParserTest {

	static Logger log = LoggerFactory.getLogger(FastParserTest.class);
	public String INPUT = "84.190.232.52 - - [20/May/2011:22:57:57 +0200] \"GET / HTTP/1.1\" 200 2950 \"-\" \"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_6_7) AppleWebKit/534.24 (KHTML, like Gecko) Chrome/11.0.696.68 Safari/534.24\"";
	public String format = "%(REMOTE_ADDR) %(HOST) %(REMOTE_USER) [%(DAY)/%(MONTH)/%(YEAR):%(TIME)] \"%(METHOD) %(URI) %(PROTOCOL)\" %(STATUS) %(SIZE) \"%(d)\" \"%(USER_AGENT)\"";

	Integer rounds = 1000000;
	Parser<Map<String, String>> parser;
	Parser<Map<String, String>> turboParser;
	boolean output = false;

	@Before
	public void setup() {
		// format = "%(REMOTE_ADDR) %(MSG)";
		List<Token> tokens = ParserGenerator.readGrammar(format);
		parser = new GenericParser(tokens);
		turboParser = new GenericTurboParser(tokens);

		log.debug("-----Warm-Up-----");
		this.testSpeed();
		this.testSpeedTurbo();
		log.debug("-----Warm-Up-----");
		output = true;
	}

	@Test
	public void test() {

		try {
			Map<String, String> out1 = parser.parse(INPUT);
			Map<String, String> out2 = turboParser.parse(INPUT);

			Assert.assertNotNull(out1);
			log.info("First parse: {}", out1);
			Assert.assertNotNull(out2);
			log.info("Second parse: {}", out2);

			Assert.assertEquals(out1.size(), out2.size());

			for (String k : out1.keySet()) {
				Assert.assertTrue(out2.containsKey(k));
				Assert.assertEquals(out1.get(k), out2.get(k));
			}

			log.info("both parsers produce same output.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testSpeed() {

		if (output)
			log.info("Testing speed of old GenericParser");

		try {
			Long start = System.currentTimeMillis();
			for (int r = 0; r < rounds; r++) {
				parser.parse(INPUT);
			}

			Long end = System.currentTimeMillis();
			if (output) {
				log.info("Processed {} items in {} ms", rounds, (end - start));
				log.info("Rate is {}/second", rounds
						/ ((end - start) / 1000.0d));
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail("Test failed: " + e.getMessage());
		}
	}

	@Test
	public void testSpeedTurbo() {

		if (output)
			log.info("Testing speed of GenericTurboParser");
		try {
			Long start = System.currentTimeMillis();
			for (int r = 0; r < rounds; r++) {
				turboParser.parse(INPUT);
			}

			Long end = System.currentTimeMillis();
			if (output) {
				log.info("Processed {} items in {} ms", rounds, (end - start));
				log.info("Rate is {}/second", rounds
						/ ((end - start) / 1000.0d));
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail("Test failed: " + e.getMessage());
		}
	}
}
