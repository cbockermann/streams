package stream.io;

import stream.Data;
import stream.data.DataFactory;

public class IndexStream extends AbstractStream {

	private long index = 0;

	@Override
	public Data readNext() throws Exception {
		Data d = DataFactory.create();
		d.put("index", index++);
		return d;
	}
}
