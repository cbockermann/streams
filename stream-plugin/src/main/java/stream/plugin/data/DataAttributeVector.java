/**
 * 
 */
package stream.plugin.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

import stream.data.Conventions;
import stream.data.Data;

import com.rapidminer.operator.Annotations;
import com.rapidminer.operator.IOObject;
import com.rapidminer.streaming.ioobject.AttributeValue;
import com.rapidminer.streaming.ioobject.AttributeVector;
import com.rapidminer.streaming.ioobject.StreamingAttributeHeader;

/**
 * <p>
 * This class implements an AttributeVector that is backed by a Data item. All
 * actions performed on this attribute vector will be reflected in the data
 * item.
 * </p>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public class DataAttributeVector extends AttributeVector {

	/** The unique class ID */
	private static final long serialVersionUID = -5496452667243823635L;

	final Data item;

	public DataAttributeVector(Data item) {
		this.item = item;
	}

	public Data getData() {
		return item;
	}

	/**
	 * @see com.rapidminer.streaming.ioobject.AttributeVector#getAttributeHeaders()
	 */
	@Override
	public Set<StreamingAttributeHeader> getAttributeHeaders() {
		Set<StreamingAttributeHeader> headers = new LinkedHashSet<StreamingAttributeHeader>();

		for (String key : item.keySet()) {
			headers.add(ConventionMapping.map(key));
		}

		return headers;
	}

	/**
	 * @see com.rapidminer.streaming.ioobject.AttributeVector#getAttributeValue(com
	 *      .rapidminer.streaming.ioobject.StreamingAttributeHeader)
	 */
	@Override
	public AttributeValue getAttributeValue(StreamingAttributeHeader header) {
		Conventions.Key key = ConventionMapping.map(header);
		if (!item.containsKey(key.toString()))
			return header.getDefaultValue();
		return new AttributeValue(item.get(key.toString()));
	}

	/**
	 * @see com.rapidminer.streaming.ioobject.AttributeVector#getAttributeHeaderByName
	 *      (java.lang.String)
	 */
	@Override
	public StreamingAttributeHeader getAttributeHeaderByName(String name) {
		return ConventionMapping.map(name);
	}

	/**
	 * @see com.rapidminer.streaming.ioobject.AttributeVector#getAttributeValueByName
	 *      (java.lang.String)
	 */
	@Override
	public AttributeValue getAttributeValueByName(String attributeName) {

		return super.getAttributeValueByName(attributeName);
	}

	/**
	 * @see com.rapidminer.streaming.ioobject.AttributeVector#setValue(com.rapidminer
	 *      .streaming.ioobject.StreamingAttributeHeader,
	 *      com.rapidminer.streaming.ioobject.AttributeValue)
	 */
	@Override
	public AttributeValue setValue(StreamingAttributeHeader header,
			AttributeValue value) {
		Serializable val = (Serializable) value.getRaw();
		Conventions.Key key = ConventionMapping.map(header);
		item.put(key.toString(), (Serializable) val);
		return value;
	}

	/**
	 * @see com.rapidminer.streaming.ioobject.AttributeVector#removeAttribute(com
	 *      .rapidminer.streaming.ioobject.StreamingAttributeHeader)
	 */
	@Override
	public void removeAttribute(StreamingAttributeHeader header) {
		Conventions.Key key = ConventionMapping.map(header);
		item.remove(key.toString());
	}

	/**
	 * @see com.rapidminer.streaming.ioobject.AttributeVector#getAnnotations()
	 */
	@Override
	public Annotations getAnnotations() {
		return super.getAnnotations();
	}

	/**
	 * @see com.rapidminer.streaming.ioobject.AttributeVector#copy()
	 */
	@Override
	public IOObject copy() {
		try {

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(item);
			oos.close();

			ByteArrayInputStream bais = new ByteArrayInputStream(
					baos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);
			Data clone = (Data) ois.readObject();
			ois.close();

			return new DataAttributeVector(clone);
		} catch (Exception e) {
			new RuntimeException("Cloning failed: " + e.getMessage());
		}

		return null;
	}

	/**
	 * @see com.rapidminer.streaming.ioobject.AttributeVector#toString()
	 */
	@Override
	public String toString() {
		return item.toString();
	}

}
