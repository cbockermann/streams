/**
 * 
 */
package stream.runtime.setup;

import static org.junit.Assert.fail;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.ComputeGraph;
import stream.ComputeGraph.SinkRef;
import stream.io.BlockingQueue;
import stream.io.Sink;
import stream.mock.SimpleMockProcessor;
import stream.runtime.DefaultProcess;
import stream.runtime.DependencyInjection;

/**
 * @author chris
 * 
 */
public class DependencyInjectionTest {

	static Logger log = LoggerFactory.getLogger(DependencyInjectionTest.class);

	/**
	 * Test method for
	 * {@link stream.runtime.DependencyInjection#injectDependencies(stream.ComputeGraph)}
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
			for (stream.Reference r : graph.sinkRefs()) {
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
}
