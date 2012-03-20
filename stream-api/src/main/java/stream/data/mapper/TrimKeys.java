package stream.data.mapper;

import stream.data.Data;
import stream.data.DataImpl;
import stream.data.DataProcessor;

/**
 * @author blom
 * 
 */
public class TrimKeys implements DataProcessor {

	@Override
	public Data process(Data data) {
		Data d = new DataImpl();
		for (String key : data.keySet()) {
			d.put(key.trim(), data.get(key));
		}
		return d;

	}

}
