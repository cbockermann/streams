/**
 * 
 */
package stream.moa;

import java.util.ArrayList;

import weka.core.Attribute;

/**
 * @author chris
 * 
 */
public class DataInstanceHeader {

	ArrayList<Attribute> attributes = new ArrayList<Attribute>();

	public Attribute getAttribute(int idx) {
		if (idx < 0 || idx >= attributes.size())
			return null;
		return attributes.get(idx);
	}

	public String getKey(int idx) {
		Attribute attr = getAttribute(idx);
		if (attr == null)
			return null;

		return attr.name();
	}

	public Attribute getAttribute(String name) {
		for (Attribute attr : attributes) {
			if (attr.name().equals(name))
				return attr;
		}

		return null;
	}
}
