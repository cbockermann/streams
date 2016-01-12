/**
 * 
 */
package stream.generator;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;

/**
 * @author chris
 * 
 */
public class GaussLimitTest {

	static Logger log = LoggerFactory.getLogger(GaussLimitTest.class);

	@Test
	public void test() {

		GaussianStream gauss = new GaussianStream();
		gauss.setAttributes(new Double[] { 0.0, 1.0 });
		gauss.setLimit(100L);

		try {
			gauss.init();
			List<Data> items = new ArrayList<Data>();

			Data item = gauss.read();
			int max = 1000;
			while (--max > 0) {
				if (item != null) {
					items.add(item);
				}
				item = gauss.read();
			}

			log.info("Limit is {}, {} items have been read.", gauss.getLimit(),
					items.size());
			Assert.assertEquals(100L, items.size());

		} catch (Exception e) {
			e.printStackTrace();
			fail("Test failed: " + e.getMessage());
		}
	}

}
