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
package stream.runtime;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chris
 * 
 */
public class VariableContext {

	/* A global logger for this class */
	static Logger log = LoggerFactory.getLogger(VariableContext.class);

	public final static String VAR_PREFIX = "${";
	public final static String VAR_SUFFIX = "}";

	/* The variables available in this context */
	protected Map<String, String> variables = new HashMap<String, String>();

	public VariableContext() {
		this(new HashMap<String, String>());
	}

	public VariableContext(VariableContext root) {
		variables = new HashMap<String, String>();
		if (root.variables != null)
			variables.putAll(root.variables);
	}

	public VariableContext(Map<String, String> variables) {
		this.variables = variables;
	}

	public VariableContext(Properties p) {
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

	public String expand(String str) {
		return substitute(str);
	}

	private String substitute(String str) {
		String content = str;
		int start = content.indexOf(VAR_PREFIX, 0);
		while (start >= 0) {
			int end = content.indexOf(VAR_SUFFIX, start);
			if (end >= start + 2) {
				String variable = content.substring(start + 2, end);
				log.debug("Found variable: {}", variable);
				log.trace("   content is: {}", content);
				if (variables.containsKey(variable))
					content = content.substring(0, start)
							+ variables.get(variable)
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
}