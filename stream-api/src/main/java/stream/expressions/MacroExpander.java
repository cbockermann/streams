/*
 *  stream.ai
 *
 *  Copyright (C) 2011-2012 by Christian Bockermann, Hendrik Blom
 * 
 *  stream.ai is a library, API and runtime environment for processing high
 *  volume data streams. It is composed of three submodules "stream-api",
 *  "stream-core" and "stream-runtime".
 *
 *  The stream.ai library (and its submodules) is free software: you can 
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
package stream.expressions;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * This class performs a simple macro-expansion based on a pattern string and a
 * ModSecurity audit-log event.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 * 
 */
public class MacroExpander {

	/* A global logger for this class */
	static Logger log = LoggerFactory.getLogger(MacroExpander.class);

	public final static String VAR_PREFIX = "%{";
	public final static String VAR_SUFFIX = "}";

	/* The variables available in this context */
	Map<String, String> variables = new HashMap<String, String>();

	public MacroExpander() {
		this(new HashMap<String, String>());
	}

	public MacroExpander(Map<String, ?> vars) {
		variables.clear();
		for (String key : vars.keySet()) {
			variables.put(key, vars.get(key).toString());
		}
	}

	public MacroExpander(Properties p) {
		this.variables = new HashMap<String, String>();
		for (Object k : p.keySet())
			variables.put(k.toString(), p.getProperty(k.toString()));
	}

	public void addVariables(Map<String, String> vars) {
		for (String key : vars.keySet())
			variables.put(key, vars.get(key));
	}

	public void set(String key, String val) {
		variables.put(key, val);
	}

	public String substitute(String str, Map<String, ? extends Object> evt) {
		String content = str;
		int start = content.indexOf(VAR_PREFIX, 0);
		while (start >= 0) {
			int end = content.indexOf(VAR_SUFFIX, start);
			if (end >= start + 2) {
				String variable = content.substring(start + 2, end);
				log.debug("Found variable: {}", variable);
				log.trace("   content is: {}", content);
				String val = get(variable.replace("data.", ""), evt);

				if (val != null)
					content = content.substring(0, start) + val
							+ content.substring(end + 1);
				else
					content = content.substring(0, start) + ""
							+ content.substring(end + 1);

				if (end < content.length())
					start = content.indexOf(VAR_PREFIX, end);
				else
					start = -1;
			} else
				start = -1;
		}
		return content;
	}

	public String get(String variable, Map<String, ?> evt) {
		if (evt != null) {
			Object str = evt.get(variable);
			if (str != null)
				return str.toString();
		}
		return variables.get(variable);
	}

	public String expand(String str) {
		return substitute(str, this.variables);
	}

	public static String expand(String string, Map<String, ?> vars) {
		MacroExpander expander = new MacroExpander(vars);
		return expander.substitute(string, null);
	}

}