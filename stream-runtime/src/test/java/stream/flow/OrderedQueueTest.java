/**
 * 
 */
package stream.flow;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chris
 * 
 */
public class OrderedQueueTest {

	static Logger log = LoggerFactory.getLogger(OrderedQueueTest.class);

	@Test
	public void test() throws Exception {

		Integer limit = 100;
		System.setProperty("limit", limit.toString());
		//
		// URL url =
		// OrderedQueueTest.class.getResource("/test-ordered-queue.xml");
		// ProcessContainer c = new ProcessContainer(url);
		//
		// long time = c.run();
		// log.info("Container required {} ms for running.", time);
		//
		// CollectorService col = c.getContext().lookup("collected",
		// CollectorService.class);
		// log.info("Collector service: {}", col);
		//
		// int colSize = col.getCollection().size();
		// log.info("Number of collected elements: {}", colSize);
		//
		// int cnt = 0;
		// for (Data item : col.getCollection()) {
		// log.info("   {}", item);
		// cnt++;
		// }
		// log.info("cnt = {}", cnt);
		//
		// Assert.assertEquals(limit.intValue(), colSize);
	}
}
