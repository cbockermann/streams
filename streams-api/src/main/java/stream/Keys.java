/**
 *
 */
package stream;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.DataFactory;
import stream.util.WildcardPattern;

/**
 * <p>
 * The <code>Keys</code> class represents a set of key strings, which may
 * include wildcard patterns using the wildcard <code>?</code> and
 * <code>*</code>. This allows for quickly specifying a list of keys from a data
 * item.
 * </p>
 *
 * <p>
 * The basic data structure used within the <em>streams</em> framework is a
 * hashmap. Each message in <em>streams</em> is represented by a hashmap that
 * maps keys to values. We refer to these keys as <em>attributes</em>,
 * <em>features</em> or simply <em>keys</em>. The following table shows an
 * example of this message representation:
 * </p>
 *
 * <div style="margin: auto; text-align: center; width: 500px;" class="figure">
 * <table style="padding: 5px;">
 * <tr>
 * <th style="font-family: Courier; font-weight: bold; color: #666;">Key</th>
 * <th style="font-family: Courier; font-weight: bold; color: #666;">Value</th>
 * </tr>
 * <tr>
 * <td style="padding: 5px;">feature:color:read</td>
 * <td style="padding: 5px; text-align: center;">231</td>
 * </tr>
 * <tr>
 * <td style="padding: 5px;">feature:color:green</td>
 * <td style="padding: 5px; text-align: center;">192</td>
 * </tr>
 * <tr>
 * <td style="padding: 5px;">feature:color:blue</td>
 * <td style="padding: 5px; text-align: center;">28</td>
 * </tr>
 * <tr>
 * <td style="padding: 5px;">feature:width</td>
 * <td style="padding: 5px; text-align: center;">4.23195</td>
 * </tr>
 * <tr>
 * <td style="padding: 5px;">feature:height</td>
 * <td style="padding: 5px; text-align: center;">5.29473</td>
 * </tr>
 * </table>
 * </div>
 *
 * <p>
 * Sometimes it is useful, to restrict the action of a processor function to a
 * subset of the features, contained in a message or item. This is exactly, what
 * the <code>stream.Keys</code> class is suited for.
 * </p>
 *
 * <p>
 * It allows for selecting a subset of keys from a set of strings using simple
 * wildcard patterns. Only the wildcards <code>*</code> and <code>?</code> are
 * used. In addition, the exclamation mark <code>!</code> can be used to
 * explicit <em>de-select</em> a previously selected key.
 * </p>
 *
 * <p>
 * In the example of the item shown above, we can use the pattern
 * </p>
 *
 * <div style="text-align: center;">
 * <code>feature:*,!feature:width</code> </div>
 *
 * <p>
 * to select all keys, which start with the string <code>feature:</code> and
 * de-select the <code>feature:width</code> attribute.
 * </p>
 *
 * <p>
 * The parts of the match are applied in the order as they appear in the pattern
 * list. Thus, if we reverse the list to <code>!feature:width,feature:*</code>,
 * then the <code>feature:width</code> key will be include in the final list as
 * it is de-selected at first, but afterwards selected by the
 * <code>feature:*</code> part.
 * </p>
 *
 * <h2><a name="a1070ed414a3234cb51da8c0771013a7"></a>Using Regular Expressions
 * </h2>
 *
 * <p>
 * With the release of <em>streams</em> version 0.9.25, the use of regular
 * expressions is supported by the <code>stream.Keys</code> class. Regular
 * expressions can be added to the pattern list and are marked with slashes,
 * like: <code>/pattern/</code>. They can be combined with the simple wildcard
 * patterns, but not within the same pattern element.
 * </p>
 *
 * <p>
 * The following example selects all patterns with the
 * <code>feature:color</code> prefix and uses a regular expression to select the
 * pixel-features:
 * </p>
 *
 * <div style="margin: auto; text-align: center;">
 * <code>feature:color*,/pixel:\d+/</code> </div>
 *
 * <p>
 * Like the simple wildcard patterns, the regular expressions can be prefixed
 * with an exclamation mark to de-select elements from a set.
 * </p>
 *
 * <p>
 * The regular expressions match, if the pattern is somehow found at any part of
 * the attribute name. If matching of the full attribute name is required, the
 * regex anchors <code>^</code> and <code>$</code> need to be specified. As an
 * example, the pattern <code>/\d+/</code> will match all attributes, which
 * include at least one digit. To limit this to attributes which only consist of
 * digits in their names, the pattern <code>/^\d+$/</code> needs to be used.
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
     * @param ks list of
     */
    public Keys(String... ks) {
        final ArrayList<String> keyValues = new ArrayList<String>();

        for (String k : ks) {
            if (!k.trim().isEmpty()) {
                keyValues.add(k.trim());
            }
        }

        this.keyValues = keyValues.toArray(new String[keyValues.size()]);
    }

    public Keys(Collection<String> ks) {
        this(ks.toArray(new String[ks.size()]));
    }

    public Set<String> select(Data item) {
        if (item == null) {
            return new HashSet<>();
        }
        return select(item.keySet());
    }

    /**
     * Select keys from the list of names (retrieved from data item).
     *
     * @param names key set retrieved from data item
     */
    public Set<String> select(Collection<String> names) {
        return select(names, keyValues);
    }

    public final String toString() {
        StringBuffer s = new StringBuffer();

        for (String keyValue : keyValues) {
            if (keyValue != null && !keyValue.trim().isEmpty()) {
                if (s.length() > 0) {
                    s.append(",");
                }
                s.append(keyValue.trim());
            }
        }

        return s.toString();
    }

    public Data refine(Data item) {
        Data out = DataFactory.create();
        for (String key : this.select(item.keySet())) {
            out.put(key, item.get(key));
        }
        return out;
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

    /**
     * Select from key set of a data item given keys.
     *
     * @param ks   key set retrieved from a data item
     * @param keys keys given by wildcards etc.
     */
    public static Set<String> select(Collection<String> ks, String[] keys) {
        Set<String> selected = new LinkedHashSet<String>();
        if (ks == null)
            return selected;

        // if no keys defined, return all data item keys (should not happen normally)
        if (keys == null) {
            log.info("No keys defined. Key set of a data item is returned.");
            selected.addAll(ks);
            return selected;
        }

        // iterate through all keys from set of keys out of data item
        // and add those matching (wildcard) keys
        for (String key : ks) {
            if (isSelected(key, keys))
                selected.add(key);
        }

        return selected;
    }

    public boolean isSelected(String key) {
        return isSelected(key, keyValues);
    }

    /**
     * Check if given value matches any of keys.
     *
     * @param value string value of key from data item
     * @param keys array of keys (e.g. using wildcards)
     * @return true iff value matches any key; otherwise, false
     */
    public static boolean isSelected(String value, String[] keys) {

        if (keys == null || keys.length == 0) {
            return false;
        }

        boolean included = false;

        for (String pattern : keys) {
            if (pattern.startsWith("!")) {
                pattern = pattern.substring(1);
                if (included && matches(pattern, value)) {
                    included = false;
                    log.debug("Removing '{}' from selection due to pattern '!{}'", value, pattern);
                }
            } else {

                if (!included && matches(pattern, value)) {
                    included = true;
                    log.debug("Adding '{}' to selection due to pattern '{}'", value, pattern);
                }
            }
        }

        return included;
    }

    /**
     * Check if given pattern matches value.
     *
     * @param pattern pattern using regular expression and/or wildcards
     * @param value   string value
     * @return true iff match, false otherwise
     */
    private static boolean matches(String pattern, String value) {

        if (pattern.startsWith("/") && pattern.endsWith("/")) {
            //
            // handling /.../ patterns as regular expressions
            //
            Pattern p = Pattern.compile(pattern.substring(1, pattern.length() - 1));
            Matcher m = p.matcher(value);
            return m.find();
        }

        return WildcardPattern.matches(pattern, value);
    }

    /**
     * Get the strings which were used to build this object.
     *
     * @return the keys
     */
    public String[] getKeyValues() {
        return keyValues;
    }
}
