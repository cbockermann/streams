/**
 * 
 */
package stream.util.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chris
 * 
 */

public class Token {

	static Logger log = LoggerFactory.getLogger(Token.class);
	final static String TOKEN_START = "%(";
	final static String TOKEN_END = ")";

	public final boolean isVariable;
	public final String name;
	public final String value;
	public final int length;
	public final Pattern pattern;

	public Token(String name) {
		this.value = name;

		this.isVariable = value != null && value.startsWith(TOKEN_START)
				&& value.endsWith(TOKEN_END);
		if (isVariable) {
			this.name = ParserGenerator.stripMacroName(name);
			// log.info("Token name is: '{}'  value was '{}'", this.name,
			// value);
		} else
			this.name = null;
		length = value.length();

		if (!name.startsWith(TOKEN_START)) {
			Pattern p;
			try {
				log.debug("trying to treat '{}' as regular expression", name);
				p = Pattern.compile(name);
			} catch (Exception e) {
				p = null;
			}
			pattern = p;
		} else {

			Pattern p = null;
			try {
				int idx = name.indexOf("|");
				int end = name.lastIndexOf(TOKEN_END);
				if (idx >= 0 && end > idx) {
					p = Pattern.compile(name.substring(idx + 1, end));
				}
				log.debug("Created regex-token with regex = '{}'", p);
			} catch (Exception e) {
				log.debug("Failed to compile pattern: {}", e.getMessage());
				if (log.isDebugEnabled())
					e.printStackTrace();
				p = null;
			}
			pattern = p;
		}
	}

	public boolean isRegex() {
		return pattern != null;
	}

	public Pattern getPattern() {
		return pattern;
	}

	public boolean isVariable() {
		return isVariable;
	}

	public String getName() {
		if (isVariable()) {
			String str = value.substring(2, value.length() - 1);
			if (str.indexOf("|") > 0)
				return str.substring(0, str.indexOf("|"));
			return str;
		}

		return value;
	}

	public int skipLength(String str) {
		if (isRegex()) {
			log.debug("Checking skip-length for pattern '{}' on string {}",
					pattern.toString(), str);

			Matcher matcher = pattern.matcher(str);
			if (matcher.find()) {
				int start = matcher.start();
				int end = matcher.end();
				log.debug("checking string '{}'", str);
				String val = str.substring(start, end);
				log.debug("substring '{}' matches {}", val, pattern);
				return val.length();
			}
		}

		return length;
	}

	public String getValue() {
		return value;
	}
}