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
package stream.util;

import java.util.LinkedHashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;

/**
 * @author chris
 * 
 */
public class KeyFilter {

	static Logger log = LoggerFactory.getLogger(KeyFilter.class);

	public static Set<String> select(Data item, String filter) {
		if (filter == null || item == null)
			return new LinkedHashSet<String>();

		return select(item, filter.split(","));
	}

	public static Set<String> select(Data item, String[] keys) {
		Set<String> selected = new LinkedHashSet<String>();
		if (item == null)
			return selected;

		if (keys == null)
			return item.keySet();

		for (String key : item.keySet()) {
			if (isSelected(key, keys))
				selected.add(key);
		}

		return selected;
	}

	public static boolean isSelected(String key, String[] keys) {

		if (keys == null || keys.length == 0)
			return false;

		boolean included = false;

		for (String k : keys) {
			if (k.startsWith("!")) {
				k = k.substring(1);
				if (included && WildcardPattern.matches(k, key))
					included = false;
				log.debug("Removing '{}' from selection due to pattern '!{}'",
						key, k);
			} else {

				if (!included && WildcardPattern.matches(k, key)) {
					included = true;
					log.debug("Adding '{}' to selection due to pattern '{}'",
							key, k);
				}
			}
		}

		return included;
	}
}
