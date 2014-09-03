/*
 *  streams library
 *
 *  Copyright (C) 2011-2014 by Christian Bockermann, Hendrik Blom
 * 
 *  streams is a library, API and runtime environment for processing high
 *  volume data streams. It is composed of three submodules "stream-api",
 *  "stream-core" and "stream-runtime".
 *
 *  The streams library (and its submodules) is free software: you can 
 *  redistribute it and/or modify it under the terms of the 
 *  GNU Affero General Public License as published by the Free Software 
 *  Foundation, either version 3 of the License, or (at your option) any 
 *  later version.
 *
 *  The stream.ai library (and its submodules) is distributed in the hope
 *  that it will be useful, but WITHOUT ANY WARRANTY; without even the implied 
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/.
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
