/**
 * 
 */
package stream.moa;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;

import stream.data.Data;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

/**
 * @author chris
 * 
 */
public class DataInstance extends LinkedHashMap<String, Serializable> implements
		Data, Instance {

	/** The unique class ID */
	private static final long serialVersionUID = 3870702952166159597L;

	final DataInstanceHeader header;
	Instances instances;

	protected DataInstance(Data item, DataInstanceHeader header) {
		putAll(item);
		this.header = header;
	}

	/**
	 * @see weka.core.Copyable#copy()
	 */
	@Override
	public Object copy() {
		return new DataInstance(this, header);
	}

	/**
	 * @see weka.core.Instance#attribute(int)
	 */
	@Override
	public Attribute attribute(int index) {
		return header.getAttribute(index);
	}

	/**
	 * @see weka.core.Instance#attributeSparse(int)
	 */
	@Override
	public Attribute attributeSparse(int indexOfIndex) {
		return null;
	}

	/**
	 * @see weka.core.Instance#classAttribute()
	 */
	@Override
	public Attribute classAttribute() {
		Attribute label = header.getAttribute("@label");
		if (label == null) {
			label = new Attribute("@label");
			header.attributes.add(label);
		}
		return label;
	}

	/**
	 * @see weka.core.Instance#classIndex()
	 */
	@Override
	public int classIndex() {

		Attribute label = classAttribute();

		Serializable value = get(label.name());
		if (value == null)
			value = "?";

		int idx = label.indexOfValue(value.toString());
		if (idx < 0)
			idx = label.addStringValue(value.toString());

		return idx;
	}

	/**
	 * @see weka.core.Instance#classIsMissing()
	 */
	@Override
	public boolean classIsMissing() {
		return !containsKey("@label");
	}

	/**
	 * @see weka.core.Instance#classValue()
	 */
	@Override
	public double classValue() {
		double value = 0.0;
		Attribute label = this.classAttribute();
		if (label.isString() || label.isNominal()) {
			String val = get(label.name() + "") + "";
			value = label.indexOfValue(val);
			if (value < 0.0) {
				value = label.addStringValue(val);
			}

		} else
			return new Double(get(label.name()) + "");
		return value;
	}

	/**
	 * @see weka.core.Instance#dataset()
	 */
	@Override
	public Instances dataset() {
		return this.instances;
	}

	/**
	 * @see weka.core.Instance#deleteAttributeAt(int)
	 */
	@Override
	public void deleteAttributeAt(int position) {
		Attribute attr = header.attributes.remove(position);
		remove(attr.name());
	}

	/**
	 * @see weka.core.Instance#enumerateAttributes()
	 */
	@Override
	public Enumeration<?> enumerateAttributes() {
		final DataInstanceHeader head = header;

		return new Enumeration<Object>() {

			int idx = 0;

			@Override
			public boolean hasMoreElements() {
				return idx < head.attributes.size();
			}

			@Override
			public Object nextElement() {
				return head.attributes.get(idx++);
			}
		};
	}

	/**
	 * @see weka.core.Instance#equalHeaders(weka.core.Instance)
	 */
	@Override
	public boolean equalHeaders(Instance inst) {
		return false;
	}

	/**
	 * @see weka.core.Instance#equalHeadersMsg(weka.core.Instance)
	 */
	@Override
	public String equalHeadersMsg(Instance inst) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see weka.core.Instance#hasMissingValue()
	 */
	@Override
	public boolean hasMissingValue() {
		return header.attributes.size() != size();
	}

	/**
	 * @see weka.core.Instance#index(int)
	 */
	@Override
	public int index(int position) {
		return 0;
	}

	/**
	 * @see weka.core.Instance#insertAttributeAt(int)
	 */
	@Override
	public void insertAttributeAt(int position) {
	}

	/**
	 * @see weka.core.Instance#isMissing(int)
	 */
	@Override
	public boolean isMissing(int attIndex) {

		Attribute attr = header.getAttribute(attIndex);
		if (attr == null)
			return false;

		return !containsKey(attr.name());
	}

	/**
	 * @see weka.core.Instance#isMissingSparse(int)
	 */
	@Override
	public boolean isMissingSparse(int indexOfIndex) {
		return false;
	}

	/**
	 * @see weka.core.Instance#isMissing(weka.core.Attribute)
	 */
	@Override
	public boolean isMissing(Attribute att) {
		return !containsKey(att.name());
	}

	/**
	 * @see weka.core.Instance#mergeInstance(weka.core.Instance)
	 */
	@Override
	public Instance mergeInstance(Instance inst) {
		return this;
	}

	/**
	 * @see weka.core.Instance#numAttributes()
	 */
	@Override
	public int numAttributes() {
		return keySet().size();
	}

	/**
	 * @see weka.core.Instance#numClasses()
	 */
	@Override
	public int numClasses() {
		Attribute label = header.getAttribute("@label");
		if (label == null)
			return 0;

		return label.numValues();
	}

	/**
	 * @see weka.core.Instance#numValues()
	 */
	@Override
	public int numValues() {
		return keySet().size();
	}

	/**
	 * @see weka.core.Instance#replaceMissingValues(double[])
	 */
	@Override
	public void replaceMissingValues(double[] array) {
	}

	/**
	 * @see weka.core.Instance#setClassMissing()
	 */
	@Override
	public void setClassMissing() {

	}

	/**
	 * @see weka.core.Instance#setClassValue(double)
	 */
	@Override
	public void setClassValue(double value) {
		put("@label", value);
	}

	/**
	 * @see weka.core.Instance#setClassValue(java.lang.String)
	 */
	@Override
	public void setClassValue(String value) {
		put("@label", value);
	}

	/**
	 * @see weka.core.Instance#setDataset(weka.core.Instances)
	 */
	@Override
	public void setDataset(Instances instances) {
		this.instances = instances;
	}

	/**
	 * @see weka.core.Instance#setMissing(int)
	 */
	@Override
	public void setMissing(int attIndex) {
		// TODO Auto-generated method stub

	}

	/**
	 * @see weka.core.Instance#setMissing(weka.core.Attribute)
	 */
	@Override
	public void setMissing(Attribute att) {
		// TODO Auto-generated method stub

	}

	/**
	 * @see weka.core.Instance#setValue(int, double)
	 */
	@Override
	public void setValue(int attIndex, double value) {
		Attribute attr = header.getAttribute(attIndex);
		setValue(attr, value);
	}

	/**
	 * @see weka.core.Instance#setValueSparse(int, double)
	 */
	@Override
	public void setValueSparse(int indexOfIndex, double value) {
		// TODO Auto-generated method stub

	}

	/**
	 * @see weka.core.Instance#setValue(int, java.lang.String)
	 */
	@Override
	public void setValue(int attIndex, String value) {
		Attribute attr = header.getAttribute(attIndex);
		setValue(attr, value);
	}

	/**
	 * @see weka.core.Instance#setValue(weka.core.Attribute, double)
	 */
	@Override
	public void setValue(Attribute att, double value) {
		put(att.name(), value);
	}

	/**
	 * @see weka.core.Instance#setValue(weka.core.Attribute, java.lang.String)
	 */
	@Override
	public void setValue(Attribute att, String value) {
		put(att.name(), value);
	}

	/**
	 * @see weka.core.Instance#setWeight(double)
	 */
	@Override
	public void setWeight(double weight) {
		put("@weight", weight);
	}

	/**
	 * @see weka.core.Instance#relationalValue(int)
	 */
	@Override
	public Instances relationalValue(int attIndex) {
		return null;
	}

	/**
	 * @see weka.core.Instance#relationalValue(weka.core.Attribute)
	 */
	@Override
	public Instances relationalValue(Attribute att) {
		return null;
	}

	/**
	 * @see weka.core.Instance#stringValue(int)
	 */
	@Override
	public String stringValue(int attIndex) {
		Attribute attr = header.getAttribute(attIndex);
		return stringValue(attr);
	}

	/**
	 * @see weka.core.Instance#stringValue(weka.core.Attribute)
	 */
	@Override
	public String stringValue(Attribute att) {
		if (containsKey(att.name()))
			return get(att.name()).toString();
		else
			return null;
	}

	/**
	 * @see weka.core.Instance#toDoubleArray()
	 */
	@Override
	public double[] toDoubleArray() {

		ArrayList<Double> values = new ArrayList<Double>();

		for (String key : keySet()) {
			if (Number.class.isAssignableFrom(get(key).getClass())) {
				values.add(((Number) get(key)).doubleValue());
			}
		}

		double d[] = new double[values.size()];
		for (int i = 0; i < values.size(); i++) {
			d[i] = values.get(i);
		}

		return d;
	}

	/**
	 * @see weka.core.Instance#toStringNoWeight()
	 */
	@Override
	public String toStringNoWeight() {
		StringBuffer s = new StringBuffer("{");
		Iterator<String> it = keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			s.append(key + ":");
			s.append(get(key));
			if (it.hasNext())
				s.append(", ");
		}
		s.append("}");
		return s.toString();
	}

	/**
	 * @see weka.core.Instance#toString(int)
	 */
	@Override
	public String toString(int attIndex) {
		return null;
	}

	/**
	 * @see weka.core.Instance#toString(weka.core.Attribute)
	 */
	@Override
	public String toString(Attribute att) {
		if (containsKey(att.name()))
			return get(att.name()).toString();

		return "null";
	}

	/**
	 * @see weka.core.Instance#value(int)
	 */
	@Override
	public double value(int attIndex) {

		Attribute attr = header.getAttribute(attIndex);
		if (attr == null)
			return 0.0d;

		return this.value(attr);
	}

	/**
	 * @see weka.core.Instance#valueSparse(int)
	 */
	@Override
	public double valueSparse(int indexOfIndex) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * @see weka.core.Instance#value(weka.core.Attribute)
	 */
	@Override
	public double value(Attribute att) {
		if (containsKey(att.name())) {
			if (att.isNominal() || att.isString()) {
				Serializable value = get(att.name());
				int idx = att.indexOfValue(value + "");
				if (idx < 0) {
					idx = att.addStringValue(value + "");
				}
				return idx;
			}
		}

		Serializable val = this.get(att.name());
		if (val == null)
			return 0.0d;

		return new Double(get(att.name()) + "");
	}

	/**
	 * @see weka.core.Instance#weight()
	 */
	@Override
	public double weight() {
		try {
			Double d = new Double(get("@weight") + "");
			return d;
		} catch (Exception e) {
			return 1.0d;
		}
	}

	public String getKey(int i) {
		int c = 0;
		Iterator<String> it = keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			if (c == i)
				return key;
			c++;
		}
		return null;
	}

	/**
	 * @see stream.data.Data#createCopy()
	 */
	@Override
	public Data createCopy() {
		return new DataInstance(this, this.header);
	}
}