/**
 * 
 */
package stream.io;

import stream.data.Data;

/**
 * @author chris
 * 
 */
public class TimeStream extends AbstractDataStream {

	String key = "@timestamp";

	/**
	 * @see stream.io.DataStream#close()
	 */
	@Override
	public void close() {
	}

	/**
	 * @see stream.io.AbstractDataStream#readHeader()
	 */
	@Override
	public void readHeader() throws Exception {
	}

	/**
	 * @see stream.io.AbstractDataStream#readItem(stream.data.Data)
	 */
	@Override
	public Data readItem(Data instance) throws Exception {
		Long timestamp = System.currentTimeMillis();
		instance.put(key, timestamp);
		return instance;
	}
}
