package stream.data.storage;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.AbstractProcessor;
import stream.Data;
import stream.ProcessContext;

/**
 * @author Hendrik Blom
 * 
 * @param <T>
 */
public abstract class Store<T extends Serializable> extends AbstractProcessor
		implements DataService<T> {
	static Logger log = LoggerFactory.getLogger(Store.class);
	protected String[] keys;
	protected Integer capacity;
	protected Map<String, T> data;

	public Store() {
		capacity = 2000;
	}

	@Override
	public T getData(String key) {
		return data.get(key);
	}

	public String[] getKeys() {
		return keys;
	}

	public void setKeys(String[] keys) {
		this.keys = keys;
	}

	public Integer getCapacity() {
		return capacity;
	}

	public void setCapacity(Integer capacity) {
		this.capacity = capacity;
	}

	@Override
	public void init(ProcessContext ctx) throws Exception {
		super.init(ctx);
		data = new ConcurrentHashMap<String, T>(capacity);
	}

	protected abstract void addData(String[] keys, Data item);

	@Override
	public Data process(Data input) {
		if (input != null) {
			addData(keys, input);
		}
		return input;
	}

	@Override
	public void reset() throws Exception {
		data.clear();
	}

}