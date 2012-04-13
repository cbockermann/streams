package stream.data.mapper;

import stream.AbstractProcessor;
import stream.annotations.Description;
import stream.data.Data;
import stream.data.DataImpl;

/**
 * @author blom
 * 
 */
@Description(group = "Data Stream.Processing.Transformations.Attributes")
public class TrimKeys extends AbstractProcessor {

	@Override
	public Data process(Data data) {
		Data d = new DataImpl();
		for (String key : data.keySet()) {
			d.put(key.trim(), data.get(key));
		}
		return d;
	}
}