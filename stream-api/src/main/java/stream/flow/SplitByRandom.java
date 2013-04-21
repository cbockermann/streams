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
	public boolean write(Data item) throws Exception {

		int idx = random.nextInt(sinks.size());
		Sink sink = sinks.get(idx);
		if (sink != null) {
			log.debug("Sending item to sink {}", sink.getId());
			return sink.write(item);
		} else {
			log.error("No sink found for index '{}'", idx);
		}
		return false;
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

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean write(Data[] data) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean offer(Data d) {
		// TODO Auto-generated method stub
		return false;
	}
}
