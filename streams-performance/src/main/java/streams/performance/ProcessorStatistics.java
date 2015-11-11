/**
 * 
 */
package streams.performance;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.LinkedHashMap;
import java.util.Map;

import stream.Processor;

/**
 * @author chris
 *
 */
public class ProcessorStatistics implements Serializable {

	private static final long serialVersionUID = -6876491501573286370L;

	public final String className;
	public final String objectReference;

	private Long itemsProcessed = 0L;
	public double processingTime = 0;

	private Long start = 0L;
	private Long end = 0L;

	double timeMean = 0.0;
	double m2 = 0.0;
	double min = 0L;
	double max = 0L;

	public ProcessorStatistics(Processor p) {
		this(p.getClass().getName(), p);
	}

	public ProcessorStatistics(String clazz, Processor obj) {
		className = clazz;
		objectReference = obj.toString();
	}

	/**
	 * A simple clone constructor
	 * 
	 * @param p
	 */
	public ProcessorStatistics(ProcessorStatistics p) {
		this.className = p.className;
		this.objectReference = p.objectReference;
		this.itemsProcessed = new Long(p.itemsProcessed);
		this.processingTime = new Double(p.processingTime);
		this.start = new Long(p.start);
		this.end = new Long(p.end);
		this.timeMean = p.timeMean;
		this.m2 = p.m2;
	}

	public Map<String, Serializable> toMap() {
		Map<String, Serializable> map = new LinkedHashMap<String, Serializable>();
		map.put("class", className);
		map.put("ref", objectReference);
		map.put("items", itemsProcessed);
		map.put("time.min", min);
		map.put("time.max", max);
		map.put("time.avg", processingTime / itemsProcessed);
		map.put("time.total", processingTime);
		map.put("time.mean", timeMean());
		map.put("time.variance", timeVariance());
		map.put("time.start", start());
		map.put("time.end", end());
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
		double nanos = millis.doubleValue() / 1000000.0;
		addNanoDelta(nanos);
	}

	public void addNanos(Long nanoSeconds) {
		addNanoDelta(nanoSeconds.doubleValue());
	}

	private void addNanoDelta(Double nanoSeconds) {
		double nanos = nanoSeconds.doubleValue();
		long now = System.currentTimeMillis();
		Double millis = nanos / 1000000.0d;
		if (itemsProcessed == 0) {
			start = now;
			min = millis;
			max = millis;
		}
		min = Math.min(min, millis);
		max = Math.max(max, millis);

		itemsProcessed++;
		end = now;

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

	public ProcessorStatistics clone() {
		return new ProcessorStatistics(this);
	}

	public String toJSON() {
		DecimalFormatSymbols dfs = new DecimalFormatSymbols();
		dfs.setDecimalSeparator('.');
		DecimalFormat df = new DecimalFormat("0.0000", dfs);
		double avg = this.processingTime / this.itemsProcessed.doubleValue();
		return "{" + "'class':'" + this.className + "', 'min':" + df.format(min) + ", 'max':" + df.format(max)
				+ ", 'avg:'" + df.format(avg) + "}";
	}
}
