/**
 * 
 */
package stream.test;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.AbstractProcessor;
import stream.Data;
import stream.ProcessContext;
import stream.runtime.ProcessContainer;

/**
 * @author chris
 * 
 */
public class Collector extends AbstractProcessor {

	final static List<Data> collected = new ArrayList<Data>();

	static Logger log = LoggerFactory.getLogger(Collector.class);

	/**
	 * @see stream.AbstractProcessor#init(stream.ProcessContext)
	 */
	@Override
	public void init(ProcessContext ctx) throws Exception {
		super.init(ctx);
		synchronized (collected) {
			log.debug("Clearing collection (contains {} items)...",
					collected.size());
			collected.clear();
		}
		ProcessContainer c = null;
		
	
	}

	/**
	 * @see stream.Processor#process(stream.Data)
	 */
	@Override
	public Data process(Data input) {
		synchronized (collected) {
			collected.add(input);
			log.debug("Collecting item ({} items in collection)...",
					collected.size());
		}
		return input;
	}

	public static List<Data> getCollection() {
		return collected;
	}
}