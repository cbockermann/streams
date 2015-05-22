package stream.data;

import java.io.Serializable;

import stream.AbstractProcessor;
import stream.Data;
import stream.ProcessContext;

public class MinusDouble extends AbstractProcessor {

	private String key;
	private String a;
	private String b;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

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

	@Override
	public void init(ProcessContext ctx) throws Exception {
		super.init(ctx);
	}

	@Override
	public Data process(Data data) {
		Serializable as = data.get(a);
		Serializable bs = data.get(b);

		if (as != null && b != null && as instanceof Double
				&& bs instanceof Double) {
			Double res = (Double) as - (Double) bs;
			data.put(key, res);
		}
		return data;
	}
}
