/**
 * 
 */
package stream.data.mapper;

import java.util.ArrayList;
import java.util.List;

import stream.Processor;
import stream.data.Data;
import stream.data.DataUtils;
import stream.runtime.annotations.Description;

/**
 * @author chris
 * 
 */
@Description(group = "Data Stream.Processing.Transformations.Data")
public class RemoveZeroes implements Processor {

	/**
	 * @see stream.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data data) {
		List<String> remove = new ArrayList<String>();

		for (String key : data.keySet()) {

			if (DataUtils.isSpecial(key))
				continue;

			try {
				Double val = new Double(data.get(key).toString());
				if (val == 0.0d)
					remove.add(key);
			} catch (Exception e) {
			}
		}

		for (String key : remove) {
			data.remove(key);
		}

		return data;
	}
}