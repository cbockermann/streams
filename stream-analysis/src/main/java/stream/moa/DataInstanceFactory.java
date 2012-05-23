/**
 * 
 */
package stream.moa;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import weka.core.Attribute;

/**
 * @author chris
 * 
 */
public class DataInstanceFactory {

	static Logger log = LoggerFactory.getLogger(DataInstanceFactory.class);
	ArrayList<Attribute> attributes = new ArrayList<Attribute>();
	ArrayList<String> attributeNames = new ArrayList<String>();

	final DataInstanceHeader header = new DataInstanceHeader();

	public DataInstance wrap(Data item) {

		for (String key : item.keySet()) {

			if (header.getAttribute(key) == null) {
				if (!isNumerical(key, item)) {
					Attribute attr = new Attribute(key, (List<String>) null);
					log.info("Created string-attribute for {}", key);
					header.attributes.add(attr);
					Serializable value = item.get(key);
					if (value != null) {
						String val = value.toString();
						int idx = attr.indexOfValue(val);
						if (idx < 0) {
							idx = attr.addStringValue(val);
						}
					}
				} else {
					Attribute attr = new Attribute(key);
					header.attributes.add(attr);
				}
			}
		}

		DataInstance inst = new DataInstance(item, header);
		return inst;
		/*
		 * 
		 * DenseInstance inst = new DenseInstance(attributes.size()); int i = 0;
		 * for (Attribute a : attributes) { String name = attributeNames.get(i);
		 * if (isNumerical(name, item)) inst.setValue(i, (Double)
		 * item.get(name)); else { String val = item.get(name).toString(); int
		 * idx = a.indexOfValue(val); if (idx < 0) idx = a.addStringValue(val);
		 * 
		 * if (idx < 0) inst.setMissing(i); else inst.setValue(i, idx); } i++; }
		 * 
		 * for (String key : item.keySet()) { if (key.startsWith("@label")) {
		 * Serializable value = item.get(key); if
		 * (Number.class.isAssignableFrom(value.getClass())) {
		 * inst.setClassValue(((Number) value).doubleValue()); } else {
		 * inst.setClassValue(value.toString()); } } }
		 * 
		 * return inst;
		 */
	}

	public static boolean isNumerical(String key, Data item) {
		Serializable value = item.get(key);
		if (value != null && Number.class.isAssignableFrom(value.getClass())) {
			return true;
		}
		return false;
	}

	public static boolean isNominal(String key, Data item) {
		return !isNumerical(key, item);
	}
}
