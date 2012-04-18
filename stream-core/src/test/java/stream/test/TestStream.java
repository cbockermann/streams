/**
 * 
 */
package stream.test;

import java.util.Random;

import org.junit.Test;

import stream.data.Data;
import stream.data.DataFactory;
import stream.io.AbstractDataStream;

/**
 * @author chris
 * 
 */
public class TestStream extends AbstractDataStream {

	Integer numberOfKeys = 10;
	Random rnd = new Random(2012L);

	/**
	 * @see stream.io.DataStream#close()
	 */
	@Override
	public void close() {
	}

	/**
	 * @see stream.io.AbstractDataStream#initReader()
	 */
	@Override
	protected void initReader() throws Exception {
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
		if (instance == null)
			instance = DataFactory.create();

		for (int i = 0; i < numberOfKeys; i++) {
			String key = "x[" + i + "]";
			Double value = rnd.nextDouble();
			instance.put(key, value);
		}

		return instance;
	}

	@Test
	public void test() {

	}
}
