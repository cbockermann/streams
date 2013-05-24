/**
 * 
 */
package stream.storm;

import static org.junit.Assert.fail;

import java.net.URL;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.test.Collector;

/**
 * @author chris
 * 
 */
public class QueueTest {

	static Logger log = LoggerFactory.getLogger(QueueTest.class);

	@Test
	public void test() {
		try {

			final URL url = QueueTest.class.getResource("/storm-queues.xml");
			log.info("Running storm topology from {}", url);
			Thread t = new Thread() {
				public void run() {
					try {
						storm.run.main(url);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			t.start();

			while (Collector.getCollection().size() < 1000) {
				log.info("{} items collected, waiting...", Collector
						.getCollection().size());
				Thread.sleep(1000);
			}

			log.info("{} items collected.", Collector.getCollection().size());
			storm.run.stopLocalCluster();

		} catch (Exception e) {
			e.printStackTrace();
			fail("Not yet implemented");
		}
	}
}
