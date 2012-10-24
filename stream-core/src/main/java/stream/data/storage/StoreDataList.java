package stream.data.storage;

import java.util.ArrayList;

import stream.Data;

/**
 * @author Hendrik Blom
 * 
 */
public class StoreDataList extends StoreList<Data> {

	public StoreDataList() {
		super();
	}

	@Override
	protected void addData(String[] keys, Data item) {
		for (String key : keys) {
			ArrayList<Data> keyData = data.get(key);
			if (keyData == null)
				keyData = new ArrayList<Data>();
			keyData.add(item);
			data.put(key, keyData);
		}

	}
}