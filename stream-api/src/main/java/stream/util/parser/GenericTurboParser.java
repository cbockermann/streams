/*
 *  streams library
 *
 *  Copyright (C) 2011-2012 by Christian Bockermann, Hendrik Blom
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
package stream.util.parser;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.util.parser.ParserGenerator.Token;

public class GenericTurboParser extends MParser implements
		Parser<Map<String, String>> {
	static Logger log = LoggerFactory.getLogger(GenericTurboParser.class);
	Token[] tokens;
	Map<String, String> defaults = new LinkedHashMap<String, String>();

	public GenericTurboParser(List<Token> token) {
		this.tokens = token.toArray(new Token[token.size()]);
		// for (Token t : tokens) {
		// log.info("Token: '{}'", t.value);
		// }
	}

	@Override
	public Map<String, String> parse(String str) throws ParseException {
		this.pos = 0; // reset();
		Map<String, String> reads = new LinkedHashMap<String, String>();

		if (tokens.length < 1)
			return reads;

		Token token = tokens[0];
		Token next = null;

		String values[] = new String[tokens.length];
		int cur = 0;
		for (int i = 1; i < tokens.length; i++) {
			next = tokens[i];

			if (token.isVariable) {
				if (next.isVariable) {
					int idx = str.indexOf(next.value, pos);
					if (idx >= 0)
						values[cur] = str.substring(idx + next.length);
				} else {
					values[cur] = readTokenUntil(str, next.value);
				}
			} else {
				this.pos += token.length;
				values[cur] = null;
			}
			token = next;
			cur++;
		}

		for (int i = 0; i < values.length; i++) {
			if (values[i] != null) {
				reads.put(tokens[i].name, values[i]);
			}
		}

		return reads;
	}

	@Override
	public Map<String, String> getDefaults() {
		return defaults;
	}

	@Override
	public void setDefaults(Map<String, String> defaults) {
		this.defaults.clear();
		this.defaults.putAll(defaults);
	}
}
