package stream.io;

import stream.Data;
import stream.data.DataFactory;

public class SampleStream extends AbstractStream {

	/**
	 * @param url
	 */
	public SampleStream() {
		super((SourceURL) null);
	}

	@Override
	public Data readNext() throws Exception {
		Data instance = DataFactory.create();
		instance.put("x", Math.random());
		return instance;
	}
}
