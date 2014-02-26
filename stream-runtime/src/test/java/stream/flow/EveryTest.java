/**
 * 
 */
package stream.flow;

import java.net.URL;

import junit.framework.Assert;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.runtime.ProcessContainer;
import stream.test.CounterTestService;

/**
 * @author chris
 * 
 */
public class EveryTest {

	static Logger log = LoggerFactory.getLogger(EveryTest.class);

	@Test
	public void test() throws Exception {

		URL url = EveryTest.class.getResource("/test-every.xml");
		ProcessContainer c = new ProcessContainer(url);

		long time = c.run();
		log.info("Container required {} ms for running.", time);

		CounterTestService s = c.getContext().lookup("counter",
				CounterTestService.class);

		Assert.assertEquals(10, s.getCount());
	}
}
