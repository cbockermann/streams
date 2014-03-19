package stream.util;

import java.io.Serializable;
import java.util.Set;

import stream.Data;
import stream.data.DataFactory;

public class MultiData {

	
	protected Data[] data;
	protected int size;

	public MultiData(int size) {
		this.size = size;
		this.data = new Data[size];
		for (int i = 0; i < size; i++) {
			data[i] = DataFactory.create();
		}
	}

	public int size() {
		return data.length;
	}

	public boolean isEmpty() {
		return data.length == 0;
	}

	public Serializable get(Object key) {
		Serializable[] result = new Serializable[data.length];
		for (int i = 0; i < data.length; i++) {
			result[i] = data[i].get(key);
		}
		return result;

	}

	// TODO
	public Serializable put(String key, Serializable value) {
		for (int i = 0; i < data.length; i++) {
			data[i].put(key, value);
		}
		return null;
	}

	// TODO
	public Serializable remove(Object key) {
		for (int i = 0; i < data.length; i++) {
			data[i].remove(key);
		}
		return null;
	}

	public Set<String> keySet() {
		return data[0].keySet();
	}

	public Data[] get() {
		return data;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("#Data=");
		sb.append(data.length);
		sb.append(" : ");
		if(data.length>0)
			sb.append(data[0].toString());
		return sb.toString();
	}


}
