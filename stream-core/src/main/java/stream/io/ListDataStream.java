package stream.io;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stream.Processor;
import stream.data.Data;
import stream.data.DataFactory;

public class ListDataStream implements DataStream {

	final List<Processor> processors = new ArrayList<Processor>();
	List<Data> data;
	int pos = 0;

	public ListDataStream(Collection<? extends Data> items) {
		data = new ArrayList<Data>(items);
		pos = 0;
	}

	@Override
	public Map<String, Class<?>> getAttributes() {
		return new HashMap<String, Class<?>>();
	}

	@Override
	public Data readNext() throws Exception {
		return readNext(DataFactory.create());
	}

	@Override
	public Data readNext(Data datum) throws Exception {
		if (pos < data.size()) {
			datum.putAll(data.get(pos++));
			return datum;
		}

		return null;
	}

	@Override
	public void addPreprocessor(Processor proc) {
		processors.add(proc);
	}

	@Override
	public void addPreprocessor(int idx, Processor proc) {
		processors.add(idx, proc);
	}

	@Override
	public List<Processor> getPreprocessors() {
		return processors;
	}

	public void close() {
		data.clear();
	}

	/**
	 * @see stream.io.DataStream#init()
	 */
	@Override
	public void init() throws Exception {
	}
}