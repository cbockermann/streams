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
package streams.runtime;

import static org.junit.Assert.fail;

import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.io.BlockingQueue;
import stream.io.Sink;
import stream.io.Source;
import stream.mock.SimpleMockProcessor;
import stream.runtime.DefaultProcess;
import stream.runtime.DependencyInjection;
import streams.application.ComputeGraph;
import streams.application.ComputeGraph.SinkRef;

/**
 * @author chris
 * 
 */
public class DependencyInjectionTest {

	static Logger log = LoggerFactory.getLogger(DependencyInjectionTest.class);

	/**
	 * Test method for
	 * {@link stream.runtime.DependencyInjection#injectDependencies(streams.application.ComputeGraph)}
	 * .
	 */
	@Test
	public void testInjectDependencies() {

		final String ref = "mySink";
		DependencyInjection di = new DependencyInjection();
		ComputeGraph graph = new ComputeGraph();

		BlockingQueue queue = new BlockingQueue();
		graph.addQueue(ref, queue);
		DefaultProcess process = new DefaultProcess();
		SimpleMockProcessor enqueue = new SimpleMockProcessor();
		process.add(enqueue);

		graph.addReference(new SinkRef(enqueue, "output", ref));

		try {
			for (streams.application.Reference r : graph.sinkRefs()) {
				di.add(r);
			}
			di.injectDependencies(graph, null);

			Sink sink = enqueue.getOutput();
			log.info("Injected sink is: {}", sink);
			log.info("Expected sink is: '{}' = {}", ref, queue);
			Assert.assertEquals(queue, enqueue.getOutput());

		} catch (Exception e) {
			fail("Test failed: " + e.getMessage());
		}
	}

	@Test
	public void testIsSourceSetter() {
		try {
			Method m = stream.runtime.DefaultProcess.class.getMethod(
					"setInput", Source.class);
			Assert.assertTrue(DependencyInjection.isSourceSetter(m));

		} catch (Exception e) {
			e.printStackTrace();
			fail("Test failed: " + e.getMessage());
		}
	}

	@Test
	public void testIsSinkSetter() throws Exception {
		Method m = getMethod(SimpleMockProcessor.class, "setOutput");
		Assert.assertTrue(DependencyInjection.isSinkSetter(m));
	}

	@Test
	public void testIsSinkArraySetter() throws Exception {
		Method m = getMethod(SimpleMockProcessor.class, "setOutputs");
		Assert.assertTrue(DependencyInjection.isArraySetter(m, Sink.class));

		Assert.assertFalse(DependencyInjection.isArraySetter(m, Source.class));
	}

	/**
	 * This method simply returns the first method of the given class which
	 * matches the provided method name.
	 * 
	 * @param clazz
	 * @param name
	 * @return
	 */
	public static Method getMethod(Class<?> clazz, String name) {
		try {
			for (Method m : clazz.getMethods()) {
				if (m.getName().equals(name))
					return m;
			}

			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
