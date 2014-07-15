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
