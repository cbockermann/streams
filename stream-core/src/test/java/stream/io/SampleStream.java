package stream.io;

import stream.Data;

public class SampleStream extends AbstractDataStream {

	@Override
	public void close() throws Exception {
	}

	@Override
	public void readHeader() throws Exception {
	}

	@Override
	public Data readItem(Data instance) throws Exception {
		instance.put( "x", Math.random() );
		return instance;
	}
}
