/**
 * 
 */
package stream;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.util.WildcardPattern;

/**
 * @author Christian Bockermann
 * 
 */
public class Keys {

	static Logger log = LoggerFactory.getLogger(Keys.class);

	public static String joinValues(Data item, String[] keys, String glue) {
		StringBuffer s = new StringBuffer();

		for (String key : select(item, keys)) {
			Serializable value = item.get(key);
			if (value != null) {
				if (s.length() > 0) {
					s.append(glue);
				}

				s.append(value.toString());
			}
		}

		return s.toString();
	}

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
