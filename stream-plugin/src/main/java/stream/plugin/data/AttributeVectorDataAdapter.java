/**
 * 
 */
package stream.plugin.data;

import java.io.Serializable;

import stream.data.Data;
import stream.data.DataFactory;

import com.rapidminer.streaming.ioobject.AttributeValue;
import com.rapidminer.streaming.ioobject.AttributeVector;
import com.rapidminer.streaming.ioobject.StreamingAttributeHeader;

/**
 * @author chris
 * 
 */
public class AttributeVectorDataAdapter {

	public static AttributeVector createAttributeVector(Data item) {
		AttributeVector v = new AttributeVector();
		for (String key : item.keySet()) {
			StreamingAttributeHeader header = ConventionMapping.map(key);
			v.setValue(header, new AttributeValue(item.get(key)));
		}
		return v;
	}

	public static Data createDataItem(AttributeVector vector) {
		Data item = DataFactory.create();
		for (StreamingAttributeHeader header : vector.getAttributeHeaders()) {
			item.put(ConventionMapping.map(header).toString(),
					(Serializable) vector.getAttributeValue(header).getRaw());
		}
		return item;
	}
}
