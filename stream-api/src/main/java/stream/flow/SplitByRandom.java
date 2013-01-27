/**
 * 
 */
package stream.flow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.expressions.Condition;
import stream.io.Sink;

/**
 * <p>
 * A split-point that randomly distributes elements uniformly among the list of
 * registered sink.
 * </p>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public class SplitByRandom extends AbstractSplit {

	static Logger log = LoggerFactory.getLogger(SplitByRandom.class);
	protected final ArrayList<Sink> sinks = new ArrayList<Sink>();
	protected final Random random = new Random();

	/**
	 * @see stream.io.Sink#write(stream.Data)
	 */
	@Override
	public void write(Data item) throws Exception {

		int idx = random.nextInt(sinks.size());
		Sink sink = sinks.get(idx);
		if (sink != null) {
			log.debug("Sending item to sink {}", sink.getId());
			sink.write(item);
		} else {
			log.error("No sink found for index '{}'", idx);
		}
	}

	/**
	 * @see stream.flow.Split#getConditions()
	 */
	@Override
	public List<Condition> getConditions() {
		return Collections.unmodifiableList(new ArrayList<Condition>());
	}

	/**
	 * @see stream.flow.Split#add(stream.expressions.Condition, stream.io.Sink)
	 */
	@Override
	public void add(Condition condition, Sink sink) {
		sinks.add(sink);
	}
}
