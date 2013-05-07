/**
 * 
 */
package stream.io.multi;

import static org.junit.Assert.fail;

import java.net.URL;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.runtime.ProcessContainer;
import stream.test.CollectorService;

/**
 * @author chris
 * 
 */
public class MultiStreamTest {

	static Logger log = LoggerFactory.getLogger(MultiStreamTest.class);

	@Test
	public void testSingleLeft() {

		try {
			URL url = MultiStreamTest.class
					.getResource("/test-multistream-leftstream.xml");
			ProcessContainer pc = new ProcessContainer(url);
			pc.run();

			CollectorService collect = pc.getContext().lookup("collector",
					CollectorService.class);
			Assert.assertNotNull(collect);
			List<Data> coll = collect.getCollection();
			log.info("Collected {} items: {}", coll.size(), coll);
			Assert.assertEquals(6, coll.size());

		} catch (Exception e) {
			e.printStackTrace();
			fail("Test failed: " + e.getMessage());
		}
	}

	@Test
	public void testSingleRight() {

		try {
			URL url = MultiStreamTest.class
					.getResource("/test-multistream-rightstream.xml");
			ProcessContainer pc = new ProcessContainer(url);
			pc.run();

			CollectorService collect = pc.getContext().lookup("collector",
					CollectorService.class);
			Assert.assertNotNull(collect);
			List<Data> coll = collect.getCollection();
			log.info("Collected {} items: {}", coll.size(), coll);
			Assert.assertEquals(6, coll.size());

		} catch (Exception e) {
			e.printStackTrace();
			fail("Test failed: " + e.getMessage());
		}
	}

	@Test
	public void test() {

		try {
			URL url = MultiStreamTest.class
					.getResource("/test-multistream.xml");
			ProcessContainer pc = new ProcessContainer(url);
			pc.run();

			CollectorService collect = pc.getContext().lookup("collector",
					CollectorService.class);
			Assert.assertNotNull(collect);
			List<Data> coll = collect.getCollection();
			log.info("Collected {} items: {}", coll.size(), coll);
			Assert.assertEquals(12, coll.size());

		} catch (Exception e) {
			e.printStackTrace();
			fail("Test failed: " + e.getMessage());
		}
	}

}
