package stream.data;

import java.io.Serializable;

import stream.AbstractProcessor;
import stream.Data;

/**
 * @author Hendrik Blom
 * 
 */
public class UpdateData extends AbstractProcessor {

	protected String[] keys;

	public String[] getKeys() {
		return keys;
	}

	public void setKeys(String[] keys) {
		this.keys = keys;
	}

	@Override
	public Data process(Data data) {
		for (String key : keys) {
			Object o = context.get(key);
			if (o != null && o instanceof Serializable)
				data.put(key, (Serializable) o);
			else
				data.put(key, "");
		}
		return data;
	}

}
