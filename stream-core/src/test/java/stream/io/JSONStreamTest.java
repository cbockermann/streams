/**
 * 
 */
package stream.io;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;

/**
 * @author chris
 * 
 */
public class JSONStreamTest {

	static Logger log = LoggerFactory.getLogger(JSONStreamTest.class);

	@Test
	public void test() throws Exception {

		URL url = JSONStreamTest.class.getResource("/test.json");
		JSONStream stream = new JSONStream(url);

		List<Data> items = new ArrayList<Data>();

		Data item = stream.readNext();
		while (item != null) {
			if (item != null)
				items.add(item);
			log.info("Read item {}", item);
			item = stream.readNext();
		}

		Assert.assertEquals(100, items.size());
	}

}
