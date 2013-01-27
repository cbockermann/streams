/**
 * 
 */
package stream.parser;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.AbstractProcessor;
import stream.Data;
import stream.ProcessContext;
import stream.annotations.Parameter;
import stream.util.parser.Parser;
import stream.util.parser.ParserGenerator;

/**
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 * 
 */
public class ParseString extends AbstractProcessor {

	static Logger log = LoggerFactory.getLogger(ParseString.class);
	String key = null;
	String format = null;
	Parser<Map<String, String>> parser = null;

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key
	 *            the key to set
	 */
	@Parameter(description = "The key of the attribute which contains the string that is to be parsed.")
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return the format
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * @param format
	 *            the format to set
	 */
	@Parameter(description = "The grammar string to create the parser from.")
	public void setFormat(String format) {
		this.format = format;
	}

	/**
	 * @see stream.AbstractProcessor#init(stream.ProcessContext)
	 */
	@Override
	public void init(ProcessContext ctx) throws Exception {
		super.init(ctx);

		if (format != null) {
			ParserGenerator pg = new ParserGenerator(format);
			parser = pg.newParser();
		}
	}

	/**
	 * @see stream.Processor#process(stream.Data)
	 */
	@Override
	public Data process(Data input) {

		if (parser == null || input == null)
			return input;

		if (key != null && input.containsKey(key)) {

			String value = null;
			try {
				value = input.get(key).toString();
				Map<String, String> vals = parser.parse(value);
				for (String k : vals.keySet()) {
					input.put(k, vals.get(k));
				}
			} catch (Exception e) {
				log.error("Failed to parse string '{}', error: {}", value,
						e.getMessage());
				if (log.isDebugEnabled())
					e.printStackTrace();
			}
		}

		return input;
	}
}
