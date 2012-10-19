/**
 * 
 */
package stream.service;

import static org.junit.Assert.fail;

import java.net.URL;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.DataFactory;
import stream.io.QueueService;
import stream.runtime.ProcessContainer;

/**
 * @author chris
 * 
 */
public class ServiceArrayTest {

	static Logger log = LoggerFactory.getLogger(ServiceArrayTest.class);

	@Test
	public void test() {

		try {
			URL url = ServiceArrayTest.class
					.getResource("/service-array-test.xml");
			final ProcessContainer pc = new ProcessContainer(url);

			GlobalCollector gc = (GlobalCollector) pc.getContext().lookup(
					"col1", QueueService.class);
			log.info("Global collector-1 is: {}", gc);

			Thread t = new Thread() {
				public void run() {
					try {
						pc.run();
					} catch (Exception e) {
						e.printStackTrace();
						fail("Test failed: " + e.getMessage());
					}
				}
			};
			t.start();

			for (int i = 1; i < 21; i++) {

				Data item = DataFactory.create();
				item.put("@id", i);
				item.put( "@stream", "input" );
				log.info("Adding item {}", item);

				pc.dataArrived("input", item);
				Thread.sleep(250);
				log.info("Global collection has size: {}", gc.getCollection()
						.size());
				Assert.assertTrue(gc.getCollection().size() % 2 == 0);
			}
			Thread.sleep(500);

			pc.shutdown();

		} catch (Exception e) {
			e.printStackTrace();
			fail("Test failed: " + e.getMessage());
		}
	}

}
