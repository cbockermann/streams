/**
 * 
 */
package stream.runtime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.ProcessContext;
import stream.data.Data;
import stream.data.DataFactory;
import stream.util.parser.TimeParser;

/**
 * @author chris
 * 
 */
public class Monitor extends AbstractProcess {

	static Logger log = LoggerFactory.getLogger(Monitor.class);
	Long interval = 1000L;
	String intervalString = "1000ms";

	/**
	 * @return the interval
	 */
	public String getInterval() {
		return intervalString;
	}

	/**
	 * @param interval
	 *            the interval to set
	 */
	public void setInterval(String intervalString) {
		this.intervalString = intervalString;
	}

	/**
	 * @see stream.runtime.AbstractProcess#getNextItem()
	 */
	@Override
	public Data getNextItem() {
		if (lastItem == null) {
			lastItem = DataFactory.create();
		}
		return lastItem;
	}

	/**
	 * @see stream.runtime.AbstractProcess#init(stream.ProcessContext)
	 */
	@Override
	public void init(ProcessContext context) throws Exception {
		super.init(context);

		try {
			interval = TimeParser.parseTime(getInterval());
		} catch (Exception e) {
			interval = 1000L;
			throw new Exception("Failed to initialize Monitor: "
					+ e.getMessage());
		}
	}

	public Data process(Data item) {
		Data data = super.process(item);
		try {
			Thread.sleep(interval);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}
}