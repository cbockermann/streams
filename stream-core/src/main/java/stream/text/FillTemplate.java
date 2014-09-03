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
package stream.text;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.AbstractProcessor;
import stream.Data;
import stream.annotations.Parameter;

/**
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public class FillTemplate extends AbstractProcessor {

	public final static String VAR_PREFIX = "${";
	public final static String VAR_SUFFIX = "}";

	static Logger log = LoggerFactory.getLogger(FillTemplate.class);

	String key = null;
	boolean emptyStrings = true;

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
	@Parameter(required = true, description = "The attribute that contains the template to fill.")
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return the emptyStrings
	 */
	public boolean isEmptyStrings() {
		return emptyStrings;
	}

	/**
	 * @param emptyStrings
	 *            the emptyStrings to set
	 */
	@Parameter(required = false, defaultValue = "false", description = "Whether to expand non-existing variables to empty strings.")
	public void setEmptyStrings(boolean emptyStrings) {
		this.emptyStrings = emptyStrings;
	}

	/**
	 * @see stream.Processor#process(stream.Data)
	 */
	@Override
	public Data process(Data input) {
		if (key == null || !input.containsKey(key))
			return input;

		String text = input.get(key).toString();

		Map<String, String> vars = new HashMap<String, String>();
		for (String k : input.keySet()) {
			vars.put("data." + k, input.get(k).toString());
		}

		String expanded = substitute(text, emptyStrings, vars);
		input.put(key, expanded);
		return input;
	}

	private String substitute(String str, boolean emptyStrings,
			Map<String, String> vars) {
		String content = str;
		int start = content.indexOf(VAR_PREFIX, 0);
		while (start >= 0) {
			int end = content.indexOf(VAR_SUFFIX, start + 1);
			if (end >= start + 2) {
				String variable = content.substring(start + 2, end);
				log.debug("Found variable: {}", variable);
				log.trace("   content is: {}", content);
				int len = variable.length();
				if (vars.containsKey(variable)) {
					String repl = vars.get(variable);
					content = content.substring(0, start) + vars.get(variable)
							+ content.substring(end + 1);
					len = repl.length();
				} else {
					if (emptyStrings) {
						content = content.substring(0, start) + ""
								+ content.substring(end + 1);
						len = 0;
					} else {
						content = content.substring(0, start) + VAR_PREFIX
								+ variable + VAR_SUFFIX
								+ content.substring(end + 1);
					}
				}

				if (end < content.length())
					start = content.indexOf(VAR_PREFIX, start + len);
				else
					start = -1;
			} else
				start = -1;
		}
		return content;
	}
}