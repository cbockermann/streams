package stream.flow;

import java.io.Serializable;

import stream.Data;

/**
 * a Long index;
 * 
 * @author Hendrik Blom
 * 
 */
public class LongIndex extends Index {

	protected long startIndex;
	protected long index;

	@Override
	public Data process(Data data) {
		Serializable s = data.get(indexKey);
		if (s != null && s instanceof Long) {
			long id = (Long) s;
			// Start index
			if (startIndex < 0) {
				startIndex = id;
				index = 0;
				data.put(indexId, index);
				return data;
			}
			index = id - startIndex;
			data.put(indexId, index);
			return data;

		}
		data.put(indexId, index);
		return data;

	}

	@Override
	public void reset() throws Exception {
		startIndex = -1l;
		index = 0;
	}

}
