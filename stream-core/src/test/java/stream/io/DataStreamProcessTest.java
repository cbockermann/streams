/**
 * 
 */
package stream.io;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;

/**
 * @author chris
 * 
 */
public class DataStreamProcessTest {

	static Logger log = LoggerFactory.getLogger(DataStreamProcessTest.class);

	/**
	 * Test method for {@link stream.io.DataStreamProcess#readNext()}.
	 */
	@Test
	public void testReadNext() throws Exception {

		DataStreamProcess dsp = new DataStreamProcess();
		dsp.setCommand("/bin/cat /tmp/test.csv");
		dsp.setFormat("stream.io.CsvStream");

		Data item = dsp.readNext();
		while (item != null) {
			log.info("item: {}", item);
			item = dsp.readNext();
		}

		dsp.close();
	}
}