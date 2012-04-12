package stream.data.mapper;

import stream.Processor;
import stream.data.Data;
import stream.runtime.annotations.Description;

/**
 * This class implements a data-processor that removes a bunch of keys from each
 * processed data item. Keys can be specified as a list:
 * 
 * <pre>
 *    &lt;RemoveAttributes keys="a,b,c" /&gt;
 * </pre>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
@Description(group = "Data Stream.Processing.Transformations.Attributes")
public class RemoveKeys implements Processor {

	String[] keys = new String[0];

	public RemoveKeys() {
	}

	public RemoveKeys(String[] keys) {
		setKeys(keys);
	}

	public void setKeys(String[] keys) {
		this.keys = keys;
	}

	public String[] getKeys() {
		return keys;
	}

	@Override
	public Data process(Data data) {

		if (keys == null)
			return data;

		for (String key : keys)
			data.remove(key);
		return data;
	}
}