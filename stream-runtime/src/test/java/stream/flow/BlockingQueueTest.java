/**
 * 
 */
package stream.flow;

import java.net.URL;

import junit.framework.Assert;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.data.SequenceID;
import stream.runtime.ProcessContainer;
import stream.test.CollectorService;

/**
 * @author chris
 * 
 */
public class BlockingQueueTest {

	static Logger log = LoggerFactory.getLogger(BlockingQueueTest.class);

	@Test
	public void test() throws Exception {

		Integer limit = 100;
		System.setProperty("limit", limit.toString());

		URL url = BlockingQueueTest.class
				.getResource("/test-blocking-queue.xml");
		ProcessContainer c = new ProcessContainer(url);

		long time = c.run();
		log.info("Container required {} ms for running.", time);

		CollectorService col = c.getContext().lookup("collected",
				CollectorService.class);
		log.info("Collector service: {}", col);

		int colSize = col.getCollection().size();
		log.info("Number of collected elements: {}", colSize);

		int cnt = 0;
		for (Data item : col.getCollection()) {
			log.info("   {}", item);
			SequenceID id = (SequenceID) item.get("@source:item");
			log.info("     => {}",
					Long.parseLong(id.toString().substring(2), 16));
			cnt++;
		}
		log.info("cnt = {}", cnt);

		Assert.assertEquals(limit.intValue(), colSize);
	}
}
