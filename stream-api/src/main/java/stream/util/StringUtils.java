/**
 * 
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
