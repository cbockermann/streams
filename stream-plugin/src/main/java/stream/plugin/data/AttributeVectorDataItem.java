/**
 * 
 */
package stream.plugin.data;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Conventions;
import stream.data.Conventions.Key;
import stream.data.Data;

import com.rapidminer.streaming.ioobject.AttributeValue;
import com.rapidminer.streaming.ioobject.AttributeVector;
import com.rapidminer.streaming.ioobject.StreamingAttributeHeader;

/**
 * <p>
 * This class implements a Data facade for the AttributeVector of the RapidMiner
 * streaming-plugin. The facade follows the naming conventions of the stream-api
 * and implements the Data interface.
 * </p>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public class AttributeVectorDataItem implements Data {

	/** The unique class ID */
	private static final long serialVersionUID = 4570707772939341394L;

	static Logger log = LoggerFactory.getLogger(AttributeVectorDataItem.class);
	AttributeVector vector;

	public AttributeVectorDataItem(AttributeVector vector) {
		this.vector = vector;
	}

	public AttributeVector getVector() {
		return vector;
	}

	/**
	 * @see java.util.Map#clear()
	 */
	@Override
	public void clear() {

		if (vector == null)
			return;

		Set<StreamingAttributeHeader> keys = new HashSet<StreamingAttributeHeader>(
				vector.getAttributeHeaders());
		for (StreamingAttributeHeader header : keys) {
			vector.removeAttribute(header);
		}
	}

	/**
	 * @see java.util.Map#containsKey(java.lang.Object)
	 */
	@Override
	public boolean containsKey(Object key) {
		if (vector == null)
			return false;

		Key k = Conventions.createKey(key.toString());
		return vector.getAttributeHeaderByName(k.name) != null;
	}

	/**
	 * @see java.util.Map#containsValue(java.lang.Object)
	 */
	@Override
	public boolean containsValue(Object value) {

		if (vector == null)
			return false;

		for (Map.Entry<String, Serializable> entry : entrySet()) {
			if (entry.getValue().equals(value))
				return true;
		}

		return false;
	}

	/**
	 * @see java.util.Map#entrySet()
	 */
	@Override
	public Set<java.util.Map.Entry<String, Serializable>> entrySet() {
		Set<Map.Entry<String, Serializable>> entries = new LinkedHashSet<Map.Entry<String, Serializable>>();

		if (vector == null)
			return entries;

		for (StreamingAttributeHeader header : vector.getAttributeHeaders()) {
			String key = header.getName();
			Object value = header.getDefaultValue();
			Map.Entry<String, Serializable> entry = new AbstractMap.SimpleEntry<String, Serializable>(
					key, (Serializable) value);

			entries.add(entry);
		}
		return entries;
	}

	/**
	 * @see java.util.Map#get(java.lang.Object)
	 */
	@Override
	public Serializable get(Object k) {

		if (vector == null)
			return null;

		Key key = Conventions.createKey(k.toString());

		StreamingAttributeHeader header = vector
				.getAttributeHeaderByName(key.name);
		if (header == null)
			return null;

		if (header.getRole().equals(key.annotation)) {
			return (Serializable) vector.getAttributeValue(header);
		}

		return null;
	}

	/**
	 * @see java.util.Map#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		if (vector == null)
			return true;

		return vector.getAttributeHeaders().isEmpty();
	}

	/**
	 * @see java.util.Map#keySet()
	 */
	@Override
	public Set<String> keySet() {
		Set<String> keys = new LinkedHashSet<String>();
		if (vector == null)
			return keys;

		for (StreamingAttributeHeader header : vector.getAttributeHeaders()) {
			Key key = ConventionMapping.map(header);
			keys.add(key.toString());
		}

		// TODO: The returned set has no connection to the map keys, i.e.
		// removals from the set will not be reflected in the attribute-vector
		return keys;
	}

	/**
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	@Override
	public Serializable put(String k, Serializable value) {

		if (vector == null)
			return null;

		Key key = Conventions.createKey(k);
		StreamingAttributeHeader pointer = null;

		for (StreamingAttributeHeader header : vector.getAttributeHeaders()) {
			if (key.name.equals(header.getName())) {
				pointer = header;
				break;
			}
		}

		if (pointer == null)
			pointer = new StreamingAttributeHeader(key.name, 0, "attribute",
					null);

		vector.setValue(pointer, new AttributeValue(value));
		return value;
	}

	/**
	 * @see java.util.Map#putAll(java.util.Map)
	 */
	@Override
	public void putAll(Map<? extends String, ? extends Serializable> m) {
		if (vector == null) {
			throw new RuntimeException(
					"DataAttributeVector not backed by an AttributeVector!");
		}

		for (String key : m.keySet()) {
			put(key, m.get(key));
		}
	}

	/**
	 * @see java.util.Map#remove(java.lang.Object)
	 */
	@Override
	public Serializable remove(Object k) {

		if (vector == null)
			return null;

		Key key = Conventions.createKey(k.toString());
		StreamingAttributeHeader header = vector
				.getAttributeHeaderByName(key.name);
		if (header == null)
			return null;

		Serializable value = (Serializable) vector.getAttributeValue(header)
				.getRaw();
		vector.removeAttribute(header);
		return value;
	}

	/**
	 * @see java.util.Map#size()
	 */
	@Override
	public int size() {
		if (vector == null)
			return -1;

		return vector.getAttributeHeaders().size();
	}

	/**
	 * @see java.util.Map#values()
	 */
	@Override
	public Collection<Serializable> values() {
		Collection<Serializable> values = new ArrayList<Serializable>();
		if (vector == null)
			return values;

		for (StreamingAttributeHeader header : vector.getAttributeHeaders()) {
			values.add((Serializable) vector.getAttributeValue(header).getRaw());
		}
		return values;
	}
}