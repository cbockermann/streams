package stream.mock;

import java.util.Collection;
import java.util.LinkedList;

import stream.Data;
import stream.io.Barrel;

public class SimpleMockBarrel implements Barrel {

	protected String id;
	protected LinkedList<Data> list = new LinkedList<>();

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public void init() throws Exception {
	}

	@Override
	public boolean write(Data item) throws Exception {
		list.add(item);
		return true;
	}

	@Override
	public boolean write(Collection<Data> data) throws Exception {
		return true;
	}

	@Override
	public void close() throws Exception {
	}

	@Override
	public Data read() throws Exception {
		return list.pollFirst();
	}

	@Override
	public int clear() {
		int s = list.size();
		list.clear();
		return s;
	}

}
