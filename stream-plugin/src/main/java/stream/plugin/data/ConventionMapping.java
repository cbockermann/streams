/**
 * 
 */
package stream.plugin.data;

import stream.data.Annotation;
import stream.data.Conventions;
import stream.data.Conventions.Key;

import com.rapidminer.streaming.ioobject.StreamingAttributeHeader;

/**
 * @author chris
 * 
 */
public class ConventionMapping {

	public final static String DEFAULT_ATTRIBUTE_ROLE = "regular";

	/**
	 * This method maps a name to an attribute header.
	 * 
	 * @param name
	 * @return
	 */
	public static StreamingAttributeHeader map(String name) {
		Key key = Conventions.createKey(name);
		return map(key);
	}

	/**
	 * This method maps a key to a StreamingAttributeHeader.
	 * 
	 * @param key
	 * @return
	 */
	public static StreamingAttributeHeader map(Key key) {
		return new StreamingAttributeHeader(key.name, 0,
				mapToRole(key.annotation), null);
	}

	/**
	 * This method creates a Key from an attribute header.
	 * 
	 * @param header
	 * @return
	 */
	public static Key map(StreamingAttributeHeader header) {
		Key key = Conventions.createKey(mapToRole(header.getRole()),
				header.getName());
		return key;
	}

	/**
	 * This method maps an annotation to an attribute role string.
	 * 
	 * @param role
	 * @return
	 */
	private static String mapToRole(String role) {

		if (role == null)
			return DEFAULT_ATTRIBUTE_ROLE;

		if (Annotation.Label.toString().equals(role)) {
			return "label";
		}

		if (Annotation.Prediction.toString().equals(role)) {
			return "prediction";
		}

		return DEFAULT_ATTRIBUTE_ROLE;
	}
}
