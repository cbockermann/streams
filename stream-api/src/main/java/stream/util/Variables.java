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
package stream.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chris
 * 
 */
public class Variables implements Map<String, String>, Serializable {

	/** The unique class ID */
	private static final long serialVersionUID = -8120239592664368847L;

	/* A global logger for this class */
	static Logger log = LoggerFactory.getLogger(Variables.class);

	public final static String VAR_PREFIX = "${";
	public final static String VAR_SUFFIX = "}";

	Variables parentContext = null;

	/* The variables available in this context */
	protected HashMap<String, String> variables = new HashMap<String, String>();

	public Variables() {
		this(new HashMap<String, String>());
	}

	public Variables(Variables root) {
		variables = new HashMap<String, String>();
		this.parentContext = root;
	}

	public Variables(Map<String, String> variables) {
		this.variables = new HashMap<String, String>(variables);
	}

	public Variables(Properties p) {
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

	public void expandAndAdd(Map<String, String> vars) {
		Map<String, String> vals = expandAll(vars);
		for (String key : vals.keySet()) {
			variables.put(key, vals.get(key));
		}
	}

	public Map<String, String> expandAll(Map<String, String> vars) {
		Map<String, String> expanded = new LinkedHashMap<String, String>();
		for (String var : vars.keySet()) {
			expanded.put(var, expand(vars.get(var)));
		}
		return expanded;
	}

	public String expand(String str, boolean emptyStrings) {
		return substitute(str, emptyStrings);
	}

	public String expand(String str) {
		return substitute(str, false);
	}

	private String substitute(String str, boolean emptyStrings) {
		String content = str;
		int start = content.indexOf(VAR_PREFIX, 0);
		while (start >= 0) {
			int end = content.indexOf(VAR_SUFFIX, start + 1);
			if (end >= start + VAR_PREFIX.length()) {
				String variable = content.substring(
						start + VAR_PREFIX.length(), end);
				log.debug("Found variable: {}", variable);
				log.trace("   content is: {}", content);
				int len = variable.length();
				if (containsKey(variable)) {
					String repl = get(variable);
					if (repl == null)
						log.info("lookup of '{}' revealed: {}", variable, repl);
					content = content.substring(0, start) + get(variable)
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

	protected boolean containsKey(String key) {
		return variables.containsKey(key)
				|| (parentContext != null && parentContext.containsKey(key));
	}

	public String get(String key) {

		String val = variables.get(key);
		if (val != null) {
			return val;
		}

		if (parentContext != null) {
			val = parentContext.get(key);
		}

		if (val == null) {
			val = System.getProperty(key);
		}

		return val;
	}

	/**
	 * @see java.util.Map#clear()
	 */
	@Override
	public void clear() {
		variables.clear();
	}

	/**
	 * @see java.util.Map#containsKey(java.lang.Object)
	 */
	@Override
	public boolean containsKey(Object key) {
		if (parentContext != null) {
			return parentContext.containsKey(key) || variables.containsKey(key);
		}

		return variables.containsKey(key);
	}

	/**
	 * @see java.util.Map#containsValue(java.lang.Object)
	 */
	@Override
	public boolean containsValue(Object value) {
		if (parentContext != null) {
			return parentContext.containsValue(value)
					|| variables.containsValue(value);
		}

		return variables.containsValue(value);
	}

	/**
	 * @see java.util.Map#entrySet()
	 */
	@Override
	public Set<java.util.Map.Entry<String, String>> entrySet() {
		Set<java.util.Map.Entry<String, String>> entries = new LinkedHashSet<java.util.Map.Entry<String, String>>();
		if (parentContext != null) {
			entries.addAll(parentContext.entrySet());
		}
		entries.addAll(variables.entrySet());
		return entries;
	}

	/**
	 * @see java.util.Map#get(java.lang.Object)
	 */
	@Override
	public String get(Object key) {
		if (key == null)
			return null;
		return get(key.toString());
	}

	/**
	 * @see java.util.Map#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		if (parentContext != null)
			return variables.isEmpty() && parentContext.isEmpty();
		return variables.isEmpty();
	}

	/**
	 * @see java.util.Map#keySet()
	 */
	@Override
	public Set<String> keySet() {
		if (parentContext != null) {
			Set<String> keys = new TreeSet<String>(parentContext.keySet());
			keys.addAll(variables.keySet());
			return keys;
		}

		return variables.keySet();
	}

	/**
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	@Override
	public String put(String key, String value) {
		return variables.put(key, value);
	}

	/**
	 * @see java.util.Map#putAll(java.util.Map)
	 */
	@Override
	public void putAll(Map<? extends String, ? extends String> m) {
		variables.putAll(m);
	}

	/**
	 * @see java.util.Map#remove(java.lang.Object)
	 */
	@Override
	public String remove(Object key) {
		return variables.remove(key);
	}

	/**
	 * @see java.util.Map#size()
	 */
	@Override
	public int size() {
		if (parentContext == null)
			return this.variables.size();
		return this.variables.size() + this.parentContext.size();
	}

	/**
	 * @see java.util.Map#values()
	 */
	@Override
	public Collection<String> values() {
		List<String> vals = new ArrayList<String>();
		if (parentContext != null) {
			vals.addAll(parentContext.values());
		}
		vals.addAll(variables.values());
		return vals;
	}

	public static Variables load(URL url) throws IOException {

		Variables vars = new Variables();

		BufferedReader reader = new BufferedReader(new InputStreamReader(
				url.openStream()));
		String line = reader.readLine();
		while (line != null) {
			if (!line.startsWith("#")) {

				int idx = line.indexOf("=");
				if (idx > 0) {
					String key = line.substring(0, idx);
					String val = line.substring(idx + 1);
					vars.put(key, val);
				}
			}

			line = reader.readLine();
		}

		return vars;
	}
}