package stream.data;

import java.io.Serializable;

import stream.AbstractProcessor;
import stream.Data;

public class MinusLong extends AbstractProcessor {

	private String a;
	private String b;
	private String key;

	public String getA() {
		return a;
	}

	public void setA(String a) {
		this.a = a;
	}

	public String getB() {
		return b;
	}

	public void setB(String b) {
		this.b = b;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public Data process(Data data) {
		Serializable as = data.get(a);
		Serializable bs = data.get(b);
		if (as != null && as instanceof Long && bs != null
				&& bs instanceof Long) {
			Long al = (Long) as;
			Long bl = (Long) bs;

			data.put(key, (al - bl));
		}
		return data;
	}
}
