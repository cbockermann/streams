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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParserGenerator {

	static Logger log = LoggerFactory.getLogger(ParserGenerator.class);
	String grammar;

	public ParserGenerator(String grammar) {
		this.grammar = grammar;
	}

	public Map<String, String> parse(String str) throws ParseException {
		return newParser().parse(str);
	}

	public Parser<Map<String, String>> newParser() {
		return create(grammar);
	}

	public List<String> parseGrammar(String grammar) {
		List<String> toks = new ArrayList<String>();
		int off = 0;
		for (int i = 0; i < grammar.length(); i++) {
			log.debug("Parsing grammar: '{}'", grammar.substring(i));
			if (grammar.startsWith(Token.TOKEN_START, i)) {
				if (i > off) {
					String con = grammar.substring(off, i);
					log.debug("adding constant {}", con);
					toks.add(con);
				}

				int end = grammar.indexOf(Token.TOKEN_END, i + 1);
				if (end >= i) {
					String var = grammar.substring(i, end + 1);
					log.debug("Found variable {}", var);
					toks.add(var);
					off = end + 1;
					i += (var.length() - 1);
				}
			} else {
				if (grammar.indexOf(Token.TOKEN_START, i) < 0) {
					log.debug("Found no more variables, treating remainder string as constant token!");
					toks.add(grammar.substring(i));
					return toks;
				}
			}
		}

		return toks;
	}

	public Parser<Map<String, String>> create(String grammarDefinition) {

		List<Token> tokenDefs = new ArrayList<Token>();
		List<String> tokens = parseGrammar(grammarDefinition); // QuotedStringTokenizer.splitRespectQuotes(
																// grammarDefinition,
																// ' ' );
		log.debug("Grammar tokens: {}", tokens);

		for (String toks : tokens) {
			tokenDefs.add(new Token(toks));
		}

		return new GenericTurboParser(tokenDefs);
	}

	public static List<Token> readGrammar(String grammarDefinition) {
		List<Token> tokenDefs = new ArrayList<Token>();
		List<String> tokens = (new ParserGenerator(grammarDefinition))
				.parseGrammar(grammarDefinition); // QuotedStringTokenizer.splitRespectQuotes(
		// grammarDefinition,
		// ' ' );
		log.debug("Grammar tokens: {}", tokens);

		for (String toks : tokens) {
			tokenDefs.add(new Token(toks));
		}
		return tokenDefs;
	}

	public boolean isVariableToken(String str) {
		boolean var = str != null && str.startsWith(Token.TOKEN_START)
				&& str.endsWith(Token.TOKEN_END);
		log.debug("isVariableToken( {} ) = {} ", str, var);
		return var;
	}

	public static String stripMacroName(String name) {
		if (name.startsWith(Token.TOKEN_START)
				&& name.endsWith(Token.TOKEN_END)) {
			int len = name.length();
			return name.substring(2, len - 1);
		}
		return name;
	}
}