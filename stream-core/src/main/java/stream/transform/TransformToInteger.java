package stream.transform;

import java.io.Serializable;

import stream.AbstractProcessor;
import stream.Data;

public class TransformToInteger extends AbstractProcessor {

	private String[] keys;
	
	@Override
	public Data process(Data data) {
		for(String key: keys){
			Serializable s = data.get(key);
			if(s!=null && s instanceof Number)
				data.put(key, ((Number)s).intValue());
		}
		return data;
	}

}
