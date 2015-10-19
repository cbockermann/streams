/**
 * 
 */
package streams.performance;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.ProcessContext;
import stream.Processor;
import stream.ProcessorList;
import streams.logging.Message;
import streams.logging.Rlog;

/**
 * @author chris
 *
 */
public class Performance extends ProcessorList {

	static Logger log = LoggerFactory.getLogger(Performance.class);
	Rlog rlog = new Rlog();
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
	ProcessorStats[] statistics = new ProcessorStats[0];

	long ignoreFirst = 0;

	final static AtomicInteger global = new AtomicInteger(0);
	final static ArrayList<PerfStats> results = new ArrayList<PerfStats>();

	File output;
	String hostname;

	/**
	 * @see stream.ProcessorList#init(stream.ProcessContext)
	 */
	@Override
	public void init(ProcessContext context) throws Exception {

		String appId = context.resolve("application.id") + "";
		log.info("Application ID is: '{}'", appId);
		rlog.define("trace", appId);
		String pid = context.getId();
		log.info("Process ID is: '{}'", pid);
		rlog.define("process.id", context.getId());

		initStart = System.currentTimeMillis();
		super.init(context);
		initEnd = System.currentTimeMillis();
		stats.put("init.start", initStart);
		stats.put("init.end", initEnd);
		rlog.message().add(stats).send();

		statistics = new ProcessorStats[this.processors.size()];
		for (int i = 0; i < statistics.length; i++) {
			Processor p = processors.get(i);
			statistics[i] = new ProcessorStats(p.getClass().getName(), p);
		}
		hostname = InetAddress.getLocalHost().getHostName();
		global.incrementAndGet();
	}

	public Data executeInnerProcessors(Data data) {

		if (data != null) {

			int i = 0;
			for (Processor p : processors) {
				long t0 = System.nanoTime(); // .currentTimeMillis();
				data = p.process(data);
				long t1 = System.nanoTime(); // .currentTimeMillis(); //
												// .currentTimeMillis();
				// log.info(" processing time for {} is {} ms", p, (t1 - t0));

				// statistics[i].itemsProcessed++;
				// statistics[i].processingTime += (t1 - t0);

				if (items >= ignoreFirst) {
					statistics[i].addNanos((t1 - t0));
				}

				// If any nested processor returns null we stop further
				// processing.
				//
				if (data == null)
					return null;
				i++;
			}

		}
		return data;
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

		Data result = data;
		if (statistics.length > 0) {
			result = this.executeInnerProcessors(data);
		}

		lastItem = System.currentTimeMillis();

		if (every > 0 && items % every == 0) {
			logPerformance();
		}

		return result;
	}

	/**
	 * @see stream.ProcessorList#finish()
	 */
	@Override
	public void finish() throws Exception {
		log.debug("Performance.finish()...");
		finishStart = System.currentTimeMillis();
		super.finish();
		finishEnd = System.currentTimeMillis();
		stats.put("finish.start", finishStart);
		stats.put("finish.end", finishEnd);
		// rlog.send(stats);

		PerfStats perf = new PerfStats(this.items, this.firstItem, this.lastItem);
		perf.add(this.statistics);

		synchronized (results) {
			results.add(perf);
		}

		rlog.message("performance", getId()).add(stats).send();

		DecimalFormat f = new DecimalFormat("0.000");
		Long duration = lastItem - firstItem;
		Double sec = duration / 1000.0d;

		Double msPerItem = duration / (1.0 * items);
		log.info("+------------------------- Performance Report ------------------------------");
		log.info("|");
		log.info("| Performance recorded based on {} events processed in {} ms", items - ignoreFirst,
				lastItem - firstItem);
		log.info("| Average performance is {} ms/item  =>  {} items/sec", f.format(msPerItem), f.format(items / sec));
		log.info("|");
		log.info("| The following {} processes have been measured:", statistics.length);
		log.info("|");
		for (int i = 0; i < statistics.length; i++) {
			log.info("|     [{}]  {}", i, statistics[i]);
			// log.info("| {}", statistics[i].toMap());
			// rlog.send(statistics[i].toMap());
			rlog.message("processor", i).add(statistics[i].toMap()).send();
		}
		log.info("|");
		log.info("+---------------------------------------------------------------------------");
		Double millis = 1.0 * (lastItem - firstItem);
		Double seconds = millis / 1000.0;
		Long count = items;
		rlog.message("trace", "performance:" + getId() + "@" + hostname).add("items", items)
				.add("millis", millis.longValue()).add("item-per-second", (count.doubleValue() / seconds)).send();

		synchronized (results) {
			if (results.size() == global.get()) {

				//
				// All result parts collected!
				//
				long start = Long.MAX_VALUE;
				long end = 0L;
				Long items = 0L;

				for (PerfStats p : results) {
					start = Math.min(start, p.start);
					end = Math.max(end, p.end);
					items += p.items;
				}

				log.info("Perfomance logging from {} to {}", new Date(start), new Date(end));
				log.info(" {} items processed in {} ms", items, (end - start));
				seconds = (end - start) / 1000.0;
				log.info("Overall data rate is {} items/second", items.doubleValue() / seconds);

				try {
					if (output != null) {
						PrintStream p = new PrintStream(new FileOutputStream(output, true));
						p.println(items + " items, " + (end - start) + " ms, " + (items.doubleValue() / seconds)
								+ " items/sec");
						p.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				rlog.message("trace", "performance.total").add("items", items).add("millis", (end - start))
						.add("item-per-second", (items.doubleValue() / seconds)).send();
			}
		}
	}

	public void logPerformance() {
		Double millis = 1.0 * (lastItem - firstItem);
		Double seconds = millis / 1000.0;

		// Long duration = lastItem - firstItem;
		// Double itemRate = items / (duration.doubleValue() / 1000.0);
		// stats.put("duration", duration);
		// stats.put("items-per-second", itemRate);
		// rlog.send(stats);
		Long count = items;
		if (count > 1) {
			log.info("current performance: {} items/sec", (count.doubleValue() / seconds));
			Message m = rlog.message().add("performance.id", getId()).add("items", items)
					.add("millis", millis.longValue()).add("item-per-second", (count.doubleValue() / seconds));
			m.send();
		}
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
	 * @return the output
	 */
	public File getOutput() {
		return output;
	}

	/**
	 * @param output
	 *            the output to set
	 */
	public void setOutput(File output) {
		this.output = output;
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

	/**
	 * @return the ignoreFirst
	 */
	public long getIgnoreFirst() {
		return ignoreFirst;
	}

	/**
	 * @param ignoreFirst
	 *            the ignoreFirst to set
	 */
	public void setIgnoreFirst(long ignoreFirst) {
		this.ignoreFirst = ignoreFirst;
	}

	public static class ProcessorStats {
		public final String className;
		public final String objectReference;

		private Long itemsProcessed = 0L;
		public double processingTime = 0;

		private Long start = 0L;
		private Long end = 0L;

		double timeMean = 0.0;
		double m2 = 0.0;

		public ProcessorStats(String clazz, Processor obj) {
			className = clazz;
			objectReference = obj.toString();
		}

		/**
		 * A simple clone constructor
		 * 
		 * @param p
		 */
		public ProcessorStats(ProcessorStats p) {
			this.className = p.className;
			this.objectReference = p.objectReference;
			this.itemsProcessed = new Long(p.itemsProcessed);
			this.processingTime = new Double(p.processingTime);
			this.start = new Long(p.start);
			this.end = new Long(p.end);
			this.timeMean = p.timeMean;
			this.m2 = p.m2;
		}

		public Map<String, Object> toMap() {
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			map.put("class", className);
			map.put("ref", objectReference);
			map.put("itemsProcessed", itemsProcessed);
			map.put("processingTime", processingTime);
			map.put("processingTime.mean", timeMean());
			map.put("processingTime.variance", timeVariance());
			map.put("start", start());
			map.put("end", end());
			return map;
		}

		public String toString() {
			DecimalFormat fmt = new DecimalFormat("0.000");
			DecimalFormat f = new DecimalFormat("######");
			Double t = processingTime;
			Double count = 1.0 * itemsProcessed;
			return f.format(processingTime) + " ms, " + fmt.format(t / count) + " ms/item => "
					+ fmt.format(count / (t / 1000.0)) + " items/sec   " + objectReference;
		}

		public void addMillis(Long millis) {
			if (itemsProcessed == 0) {
				start = System.currentTimeMillis();
			}

			itemsProcessed++;
			end = System.currentTimeMillis();
			processingTime += millis;
			double delta = millis - timeMean;
			timeMean = timeMean + delta * 0.5;
			m2 = m2 + delta * (millis - timeMean);
		}

		public void addNanos(Long nanoSeconds) {
			double nanos = nanoSeconds.doubleValue();
			Double millis = nanos / 1000000.0d;
			if (itemsProcessed == 0) {
				start = System.currentTimeMillis();
			}

			itemsProcessed++;
			end = System.currentTimeMillis();

			processingTime += (millis);
			double delta = millis - timeMean;
			timeMean = timeMean + delta * 0.5;
			m2 = m2 + delta * (millis - timeMean);
		}

		public long itemsProcessed() {
			return itemsProcessed;
		}

		public double processingTime() {
			return processingTime;
		}

		public long start() {
			return start.longValue();
		}

		public long end() {
			return end.longValue();
		}

		public double timeMean() {
			return timeMean;
		}

		public double timeVariance() {
			if (itemsProcessed < 2)
				return 0.0;
			return m2 / (itemsProcessed - 1);
		}

		public ProcessorStats clone() {
			return new ProcessorStats(this);
		}
	}

	public static class PerfStats {

		final List<ProcessorStats> procStats = new ArrayList<ProcessorStats>();
		final long items;
		final long start;
		final long end;

		public PerfStats(long items, long start, long end) {
			this.items = items;
			this.start = start;
			this.end = end;
		}

		public void add(ProcessorStats[] processorStats) {
			for (int i = 0; i < processorStats.length; i++) {
				procStats.add(processorStats[i]);
			}
		}
	}
}