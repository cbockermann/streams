package stream.util;

/**
 * @author Hendrik Blom
 * 
 */
public class Keys {

	public static String create(String sep, String... values) {
		StringBuilder b = new StringBuilder();
		int i = 0;
		for (String value : values) {
			i++;
			b.append(value);
			if (i < values.length)
				b.append(sep);
		}
		return b.toString();
	}
}