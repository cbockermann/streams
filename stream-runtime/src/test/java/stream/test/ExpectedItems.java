/**
 * 
 */
package stream.test;

import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.AbstractProcessor;
import stream.Data;

/**
 * @author chris
 * 
 */
public class ExpectedItems extends AbstractProcessor {

	public final static AtomicBoolean finishMethodPerformed = new AtomicBoolean(
			false);

	static Logger log = LoggerFactory.getLogger(ExpectedItems.class);
	long count = 0L;
	long seen = 0L;

	/**
	 * @see stream.Processor#process(stream.Data)
	 */
	@Override
	public Data process(Data input) {
		if (input != null) {
			seen++;
		}
		return input;
	}

	/**
	 * 
	 * @see stream.AbstractProcessor#finish()
	 */
	@Override
	public void finish() throws Exception {
		log.info("finishing processor - {} items seen, {} expected.", seen,
				count);
		finishMethodPerformed.set(true);
	}

	/**
	 * @return the count
	 */
	public long getCount() {
		return count;
	}

	/**
	 * @param count
	 *            the count to set
	 */
	public void setCount(long count) {
		this.count = count;
	}

}
