/**
 * 
 */
package stream.data.mapper;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import stream.Processor;
import stream.data.Data;
import stream.data.DataImpl;

/**
 * @author chris
 * 
 */
public class SelectAttributes implements Processor {

	String[] keys = null;

	Set<String> selected = new HashSet<String>();
	private Boolean remove;

	public SelectAttributes() {
		super();
		this.remove = true;
	}

	public void setKeys(String[] keys) {
		this.keys = keys;
		for (String key : keys)
			selected.add(key);
	}

	public Processor setKeys(Set<String> keys) {
		this.keys = keys.toArray(new String[keys.size()]);
		selected = keys;
		return this;
	}

	public String[] getKeys() {
		return keys;
	}

	public void setRemove(Boolean remove) {
		this.remove = remove;
	}

	public Boolean getRemove() {
		return this.remove;
	}

	/**
	 * @see stream.DataProcessor#process(stream.data.Data)
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
