package stream.transform;

import java.io.Serializable;

import stream.AbstractProcessor;
import stream.Data;

/**
 * 
 * Transform the values of the given keys to Integer, if the values are a
 * instance of Number.
 * 
 * @author Hendrik Blom
 * 
 */
public class TransformToInteger extends AbstractProcessor {

	private String[] keys;

	@Override
	public Data process(Data data) {
		for (String key : keys) {
			Serializable s = data.get(key);
			if (s != null && s instanceof Number)
				data.put(key, ((Number) s).intValue());
		}
		return data;
	}

}
