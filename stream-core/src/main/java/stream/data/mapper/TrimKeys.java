package stream.data.mapper;

import stream.data.AbstractDataProcessor;
import stream.data.Data;
import stream.data.DataImpl;
import stream.runtime.annotations.Description;

/**
 * @author blom
 * 
 */
@Description(group = "Data Stream.Processing.Transformations.Attributes")
public class TrimKeys extends AbstractDataProcessor {

	@Override
	public Data process(Data data) {
		Data d = new DataImpl();
		for (String key : data.keySet()) {
			d.put(key.trim(), data.get(key));
		}
		return d;
	}
}