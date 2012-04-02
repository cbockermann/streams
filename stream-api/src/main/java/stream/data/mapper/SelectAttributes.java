/**
 * 
 */
package stream.data.mapper;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import stream.data.AbstractDataProcessor;
import stream.data.Data;
import stream.data.DataImpl;
import stream.data.DataProcessor;
import stream.util.ParameterUtils;

/**
 * @author chris
 * 
 */
public class SelectAttributes extends AbstractDataProcessor {

	String[] keys = new String[0];

	Set<String> selected = new HashSet<String>();
	private Boolean remove;

	public SelectAttributes() {
		super();
		this.remove = true;
	}

	public void setKeys(String keyString) {
		keys = ParameterUtils.split(keyString);
		for (String key : keys)
			selected.add(key);
	}

	public DataProcessor setKeys(Set<String> keys) {
		this.keys = keys.toArray(new String[keys.size()]);
		selected = keys;
		return this;
	}

	public String getKeys() {
		return ParameterUtils.join(keys);
	}

	public void setRemove(Boolean remove) {
		this.remove = remove;
	}

	public Boolean getRemove() {
		return this.remove;
	}

	/**
	 * @see stream.data.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data data) {
		if (keys == null || keys.length == 0)
			return data;

		Iterator<String> it = data.keySet().iterator();
		if (remove) {
			while (it.hasNext()) {
				String key = it.next();
				if (!selected.contains(key)) {
					it.remove();
				}
			}
			return data;
		} else {
			Data result = new DataImpl();
			while (it.hasNext()) {
				String key = it.next();
				if (selected.contains(key)) {
					result.put(key, data.get(key));
				}
			}
			return result;
		}
	}
}
