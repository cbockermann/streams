/**
 * 
 */
package stream;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.util.WildcardPattern;

/**
 * <p>
 * The <code>Keys</code> class represents a set of key strings, which may
 * include wildcard patterns using the wildcard <code>?</code> and
 * <code>*</code>. This allows for quickly specifying a list of keys from a data
 * item.
 * </p>
 * 
 * @author Christian Bockermann <christian.bockermann@udo.edu>
 * 
 */
public final class Keys implements Serializable {

	/** The unique class ID */
	private static final long serialVersionUID = 1301122628686584473L;

	static Logger log = LoggerFactory.getLogger(Keys.class);

	final String[] keyValues;

	/**
	 * This constructor will split the argument string by occurences of the
	 * <code>'</code> character and create a <code>Keys</code> instance of the
	 * resulting substrings.
	 * 
	 * @param kString
	 */
	public Keys(String kString) {
		this(kString.split(","));
	}

	/**
	 * This constructor creates an instance of <code>Keys</code> with the given
	 * list of key names. Each key name may contain wildcards.
	 * 
	 * @param ks
	 */
	protected Keys(String... ks) {
		final ArrayList<String> keyValues = new ArrayList<String>();

		for (String k : ks) {
			if (k.trim().isEmpty()) {
				continue;
			} else {
				keyValues.add(k.trim());
			}
		}

		this.keyValues = keyValues.toArray(new String[keyValues.size()]);
	}

	protected Keys(Collection<String> ks) {
		final ArrayList<String> keyValues = new ArrayList<String>();

		for (String k : ks) {
			if (k.trim().isEmpty()) {
				continue;
			} else {
				keyValues.add(k.trim());
			}
		}

		this.keyValues = keyValues.toArray(new String[keyValues.size()]);
	}

	public Set<String> select(Collection<String> names) {
		return select(names, keyValues);
	}

	public final String toString() {
		StringBuffer s = new StringBuffer();

		for (int i = 0; i < keyValues.length; i++) {
			if (keyValues[i] != null && !keyValues[i].trim().isEmpty()) {
				if (s.length() > 0) {
					s.append(",");
				}
				s.append(keyValues[i].trim());
			}
		}

		return s.toString();
	}

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

		return select(item.keySet(), keys);
	}

	public static Set<String> select(Collection<String> ks, String[] keys) {
		Set<String> selected = new LinkedHashSet<String>();
		if (ks == null)
			return selected;

		if (keys == null) {
			selected.addAll(ks);
			return selected;
		}

		for (String key : ks) {
			if (isSelected(key, keys))
				selected.add(key);
		}

		return selected;
	}

	public static boolean isSelected(String value, String[] keys) {

		if (keys == null || keys.length == 0)
			return false;

		boolean included = false;

		for (String k : keys) {
			if (k.startsWith("!")) {
				k = k.substring(1);
				if (included && WildcardPattern.matches(k, value)) {
					included = false;
					log.debug(
							"Removing '{}' from selection due to pattern '!{}'",
							value, k);
				}
			} else {

				if (!included && WildcardPattern.matches(k, value)) {
					included = true;
					log.debug("Adding '{}' to selection due to pattern '{}'",
							value, k);
				}
			}
		}

		return included;
	}
}
