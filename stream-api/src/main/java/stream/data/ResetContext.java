package stream.data;

import stream.AbstractProcessor;
import stream.Data;

/**
 * @author Hendrik Blom
 * 
 */
public class ResetContext extends AbstractProcessor {

	protected String[] keys;
	protected String regexp;

	public String[] getKeys() {
		return keys;
	}

	public void setKeys(String[] keys) {
		this.keys = keys;
	}

	@Override
	public Data process(Data data) {
		if (keys == null)
			context.clear();
		else
			for (String key : keys) {
				context.set(key, null);
			}
		return data;
	}

}
