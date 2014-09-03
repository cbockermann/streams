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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * <p>
 * A set of string utility functions.
 * </p>
 * 
 * @author Christian Bockermann
 * 
 */
public final class StringUtils {

	public static String join(Collection<String> strings, String sep) {
		StringBuilder s = new StringBuilder();
		Iterator<String> it = strings.iterator();
		while (it.hasNext()) {
			String value = it.next();
			if (value != null) {
				s.append(value);
				if (it.hasNext())
					s.append(sep);
			}
		}

		return s.toString();
	}

	public static String join(String[] strings, String sep) {
		if (strings == null)
			return "";

		StringBuilder s = new StringBuilder();
		for (int i = 0; i < strings.length; i++) {
			if (strings[i] != null) {
				s.append(strings[i]);
				if (i + 1 < strings.length && strings[i] != null)
					s.append(sep);
			}
		}
		return s.toString();
	}

	public static List<String> split(String str, String regex) {
		List<String> list = new ArrayList<String>();
		if (str == null)
			return list;

		String[] tok = str.split(regex);
		for (String t : tok) {
			list.add(t);
		}
		return list;
	}
}
