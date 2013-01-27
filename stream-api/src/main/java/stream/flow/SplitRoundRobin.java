/**
 * 
 */
package stream.flow;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.io.Sink;

/**
 * <p>
 * A split-point following a round-robin strategy.
 * </p>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public class SplitRoundRobin extends SplitByRandom {

	static Logger log = LoggerFactory.getLogger(SplitRoundRobin.class);

	protected AtomicInteger lastIndex = new AtomicInteger(0);

	/**
	 * @see stream.flow.SplitByRandom#write(stream.Data)
	 */
	@Override
	public void write(Data item) throws Exception {

		int idx = lastIndex.getAndIncrement();
		Sink destination = sinks.get(idx % sinks.size());
		log.debug("Current index '{}' ~> {}", idx % sinks.size(), destination);

		destination.write(item);
	}
}
