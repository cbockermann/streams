/**
 * 
 */
package stream.runtime.setup;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.io.DataStream;

/**
 * @author chris
 * 
 */
public class DataStreamFactoryTest {

	static Logger log = LoggerFactory.getLogger(DataStreamFactoryTest.class);

	/**
	 * Test method for
	 * {@link stream.runtime.setup.DataStreamFactory#createStream(java.util.Map)}
	 * .
	 */
	@Test
	public void testCreateStreamFromMap() throws Exception {

		try {
			Map<String, String> setup = new HashMap<String, String>();

			setup.put("class", "stream.io.SQLStream");
			setup.put("url", "jdbc:hsqldb:res:test.db");
			setup.put("username", "SA");
			setup.put("password", "");
			setup.put("select", "SELECT * FROM TEST_TABLE");

			DataStream stream = DataStreamFactory.createStream(setup);

			Data item = stream.readNext();
			while (item != null) {
				log.info("Data item: {}", item);
				item = stream.readNext();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
}