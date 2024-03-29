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
 * Statistics for some processor such as number of processed items, time used
 * for those items to be processed and so on.
 *
 * @author chris
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
    double min = 0d;
    double max = 0d;

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
     * @param statistics
     */
    public ProcessorStatistics(ProcessorStatistics statistics) {
        this.className = statistics.className;
        this.objectReference = statistics.objectReference;
        this.itemsProcessed = statistics.itemsProcessed;
        this.processingTime = statistics.processingTime;
        this.start = statistics.start;
        this.end = statistics.end;
        this.timeMean = statistics.timeMean;
        this.m2 = statistics.m2;
    }

    /**
     * Reset statistics collected so far (used for mergeable processor stats)
     */
    public void reset() {
        itemsProcessed = 0L;
        processingTime = 0d;
        start = 0L;
        end = 0L;
        timeMean = 0d;
        m2 = 0d;
    }

    /**
     * Put statistic values into a serializable map.
     *
     * @return map containing values about processor
     */
    public Map<String, Serializable> toMap() {
        Map<String, Serializable> map = new LinkedHashMap<>();
        map.put("class", className);
        map.put("ref", objectReference);
        map.put("items", itemsProcessed);
        map.put("time.min", min);
        map.put("time.max", max);
        map.put("time.avg", processingTime / itemsProcessed);
        map.put("time.total", processingTime);
        map.put("time.mean", timeMean);
        map.put("time.variance", timeVariance());
        map.put("time.start", start);
        map.put("time.end", end);
        return map;
    }

    /**
     * Merge current statistics with given new values. This can be used on the server side
     * (performance receiver) in order to merge new statistics.
     */
    public synchronized void mergeStatistics(ProcessorStatistics data) {
        itemsProcessed += data.itemsProcessed();
        min = Math.min(min, data.min);
        max = Math.max(max, data.max);
        start = Math.min(start, data.start());
        end = data.end();
        processingTime += data.processingTime;
    }

    /**
     * Transform processor statistics into a string for output.
     */
    public String toString() {
        DecimalFormat fmt = new DecimalFormat("0.000");
        DecimalFormat f = new DecimalFormat("######");
        double count = itemsProcessed.doubleValue();
        return f.format(processingTime) + " ms, " + fmt.format(processingTime / count) + " ms/item => "
                + fmt.format(count / (processingTime / 1000.0)) + " items/sec   " + objectReference;
    }

    /**
     * Update statistics by adding milliseconds to processed time and counting
     * processed items.
     *
     * @param millis
     *            long value of milliseconds
     */
    public void addMillis(Long millis) {
        double nanos = millis.doubleValue() * 1000000.0;
        addNanoDelta(nanos);
    }

    /**
     * Update statistics by adding nanoseconds to processed time and counting
     * processed items.
     *
     * @param nanoSeconds
     *            long value of nanoseconds
     */
    public void addNanos(Long nanoSeconds) {
        addNanoDelta(nanoSeconds.doubleValue());
    }

    private void addNanoDelta(double nanoSeconds) {
        long now = System.currentTimeMillis();
        Double millis = nanoSeconds / 1000000.0d;
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
        return start;
    }

    public long end() {
        return end;
    }

    public double timeMean() {
        return timeMean;
    }

    public double timeVariance() {
        if (itemsProcessed < 2) {
            return 0.0;
        }
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
