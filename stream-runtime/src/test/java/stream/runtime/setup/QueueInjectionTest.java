/**
 * 
 */
package stream.runtime.setup;

import static org.junit.Assert.fail;

import java.lang.reflect.Method;

import junit.framework.Assert;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.flow.Enqueue;
import stream.io.BlockingQueue;
import stream.io.OrderedQueue;
import stream.io.Queue;
import stream.io.RandomStream;

/**
 * @author chris
 * 
 */
public class QueueInjectionTest {

	static Logger log = LoggerFactory.getLogger(QueueInjectionTest.class);

	/**
	 * Test method for
	 * {@link stream.runtime.setup.QueueInjection#hasSinkSetter(java.lang.String, java.lang.Object)}
	 * .
	 */
	@Test
	public void testHasSinkSetter() {
		Enqueue enq = new Enqueue();
		Assert.assertNotNull(QueueInjection.hasSinkSetter("queue", enq));
	}

	/**
	 * Test method for
	 * {@link stream.runtime.setup.QueueInjection#isQueueSetter(java.lang.reflect.Method)}
	 * .
	 */
	@Test
	public void testIsQueueSetter() {
		try {
			Enqueue enq = new Enqueue();
			Method setQueue = enq.getClass().getMethod("setQueue", Queue.class);
			Assert.assertTrue(QueueInjection.isQueueSetter(setQueue));
		} catch (Exception e) {
			e.printStackTrace();
			fail("Test failed: " + e.getMessage());
		}
	}

	/**
	 * Test method for
	 * {@link stream.runtime.setup.QueueInjection#isQueueArraySetter(java.lang.reflect.Method)}
	 * .
	 */
	@Test
	public void testIsQueueArraySetter() {
		try {
			Enqueue enq = new Enqueue();
			Method setQueues = null;

			for (Method m : enq.getClass().getMethods()) {
				if (m.getName().equals("setQueues")) {
					setQueues = m;
					break;
				}
			}
			Assert.assertNotNull(setQueues);
			// Assert.assertTrue(QueueInjection.isQueueArraySetter(setQueues));
		} catch (Exception e) {
			e.printStackTrace();
			fail("Test failed: " + e.getMessage());
		}
	}

	/**
	 * Test method for
	 * {@link stream.runtime.setup.QueueInjection#isSink(java.lang.Class)}.
	 */
	@Test
	public void testIsSink() {
		log.info("Testing if 'BlockingQueue' is a sink...");
		Assert.assertTrue(QueueInjection.isSink(BlockingQueue.class));

		log.info("Testing if 'BlockingQueue' is a sink...");
		Assert.assertTrue(QueueInjection.isSink(OrderedQueue.class));

		log.info("Testing if 'Enqueue' is a sink...");
		Assert.assertFalse(QueueInjection.isSink(Enqueue.class));

		log.info("Testing if 'RandomStream' is a sink...");
		Assert.assertFalse(QueueInjection.isSink(RandomStream.class));
	}

	/**
	 * Test method for
	 * {@link stream.runtime.setup.QueueInjection#isQueue(java.lang.Class)}.
	 */
	@Test
	public void testIsQueue() {
		Assert.assertTrue(QueueInjection.isQueue(BlockingQueue.class));
		Assert.assertFalse(QueueInjection.isQueue(RandomStream.class));
	}
}
