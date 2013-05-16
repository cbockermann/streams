/**
 * 
 */
package stream.io;

import stream.Data;
import stream.data.DataFactory;

/**
 * @author chris
 * 
 */
public class ConstantStreamMock extends AbstractStream {

	final Data item;

	public ConstantStreamMock() {
		item = DataFactory.create();
		item.put("id", new Long(1L));
		item.put("name", "streams Framework");
		item.put("data", new String[] { "v1", "v2", "v3" });
	}

	/**
	 * @see stream.io.AbstractStream#readNext()
	 */
	@Override
	public Data readNext() throws Exception {
		return DataFactory.create(item);
	}
}