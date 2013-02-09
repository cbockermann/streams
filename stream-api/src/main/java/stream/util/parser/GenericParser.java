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

public class GenericParser extends MParser implements
		Parser<Map<String, String>> {
	static Logger log = LoggerFactory.getLogger(GenericParser.class);
	List<Token> tokens;
	Map<String, String> defaults = new LinkedHashMap<String, String>();

	public GenericParser(List<Token> token) {
		this.tokens = token;
	}

	@Override
	public Map<String, String> parse(String str) throws ParseException {
		this.reset();
		Map<String, String> reads = new LinkedHashMap<String, String>();
		reads.putAll(defaults);

		for (int i = 0; i < tokens.size(); i++) {
			Token token = tokens.get(i);
			Token next = null;
			if (i + 1 < tokens.size())
				next = tokens.get(i + 1);
			// skipBlanks( str );
			log.debug("Remainder string: '{}'", remainder(str));

			if (!token.isVariable()) {
				log.debug("Next token is a constant '{}'", token.getValue());
				int start = pos;

				if (token.isRegex()) {
					int len = token.skipLength(remainder(str));
					this.pos += len;
				} else {
					String val = prefix(str, token.getValue().length());
					log.debug("   const read: '{}'", val);
					if (!token.getValue().equals(val))
						throw new ParseException("Failed to read '"
								+ token.getValue() + "', found: " + val
								+ " at position " + start + " of string: '"
								+ str + "'");
					else
						this.pos += token.getValue().length();
				}
			} else {
				log.debug("Next token is a variable '{}'", token.getValue());
				String val = null;

				if (token.isRegex()) {
					String rem = remainder(str);
					int len = token.skipLength(rem);
					val = rem.substring(0, len);
					this.pos += len;
					reads.put(token.getName(), val);
				} else {
					if (next != null && !next.isVariable())
						val = readTokenUntil(str, next.getValue());
					else {
						if (next == null)
							val = remainder(str);
						else
							val = readToken(str);
					}
					if (log.isDebugEnabled()) {
						log.debug("   {} = '{}'", token.getValue(), val);
						log.debug("   {} = '{}'", strip(token.getValue()), val);
						log.debug("remainder: '{}'", remainder(str));
					}
					reads.put(strip(token.value), val);
				}
			}
		}

		return reads;
	}

	public String strip(String name) {
		if (name.startsWith(Token.TOKEN_START)
				&& name.endsWith(Token.TOKEN_END)) {
			int len = name.length();
			return name.substring(2, len - 1);
		}
		return name;
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
