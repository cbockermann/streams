package stream.data.mapper;

import stream.AbstractProcessor;
import stream.annotations.Parameter;
import stream.data.Data;
import stream.data.DataUtils;

public class HideKey extends AbstractProcessor {

	String key;

	public String getKey() {
		return key;
	}

	@Parameter(name = "key")
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @see stream.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data data) {
		DataUtils.hide(key, data);
		return data;
	}
}