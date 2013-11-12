package stream.data;

import java.io.Serializable;

import stream.AbstractProcessor;
import stream.Data;
import stream.service.Service;

/**
 * @author Hendrik Blom
 *
 */
public class UpdateContext extends AbstractProcessor implements Service {

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
		for (String key : keys) {
			Serializable s = data.get(key);
			if (s != null &&!s.toString().equals("--"))
				context.set(key, s);
		}
		return data;
	}

	@Override
	public void reset() throws Exception {
		for (String key : keys) {
			context.set(key, null);
		}

	}


}
