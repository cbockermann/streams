/**
 * 
 */
package stream.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import stream.Data;
import stream.Processor;

/**
 * @author Christian Bockermann
 * 
 */
public class Collector implements Processor, CollectorService {

	final ArrayList<Data> collected = new ArrayList<Data>();

	/**
	 * @see stream.Processor#process(stream.Data)
	 */
	@Override
	public Data process(Data input) {
		collected.add(input);
		return input;
	}

	/**
	 * @see stream.service.Service#reset()
	 */
	@Override
	public void reset() throws Exception {
		collected.clear();
	}

	/**
	 * @see stream.test.CollectorService#getCollection()
	 */
	@Override
	public List<Data> getCollection() {
		return Collections.unmodifiableList(collected);
	}
}