/**
 * 
 */
package streams.performance;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import stream.Data;
import stream.ProcessContext;
import stream.ProcessorList;

/**
 * @author chris
 *
 */
public class Performance extends ProcessorList {

	Rlog rlog;
	String id = null;
	int every = 10000;

	long initStart = 0L;
	long initEnd = 0L;

	long items = 0L;
	long firstItem = 0L;
	long lastItem = 0L;

	long finishStart = 0L;
	long finishEnd = 0L;

	Map<String, Serializable> stats = new LinkedHashMap<String, Serializable>();

	/**
	 * @see stream.ProcessorList#init(stream.ProcessContext)
	 */
	@Override
	public void init(ProcessContext context) throws Exception {
		rlog = new Rlog();
		if (id != null) {
			rlog = new Rlog(id);
		}
		initStart = System.currentTimeMillis();
		super.init(context);
		initEnd = System.currentTimeMillis();
		stats.put("init.start", initStart);
		stats.put("init.end", initEnd);
		rlog.send(stats);
	}

	/**
	 * @see stream.ProcessorList#process(stream.Data)
	 */
	@Override
	public Data process(Data data) {

		if (firstItem == 0L) {
			firstItem = System.currentTimeMillis();
		}

		items++;
		Data result = super.process(data);

		lastItem = System.currentTimeMillis();

		if (every > 0 && items % every == 0) {
			Long duration = lastItem - firstItem;
			Double itemRate = items / (duration.doubleValue() / 1000.0);
			stats.put("duration", duration);
			stats.put("items-per-second", itemRate);
			rlog.send(stats);
		}

		return result;
	}

	/**
	 * @see stream.ProcessorList#finish()
	 */
	@Override
	public void finish() throws Exception {
		finishStart = System.currentTimeMillis();
		super.finish();
		finishEnd = System.currentTimeMillis();
		stats.put("finish.start", finishStart);
		stats.put("finish.end", finishEnd);
		rlog.send(stats);
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the every
	 */
	public int getEvery() {
		return every;
	}

	/**
	 * @param every
	 *            the every to set
	 */
	public void setEvery(int every) {
		this.every = every;
	}
}