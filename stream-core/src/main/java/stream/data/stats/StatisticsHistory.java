package stream.data.stats;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>
 * This class is a container for a data monitorring based on time. It arranges
 * values along a time-line with a fixed step-size. Values added to dates
 * between steps, are added to the nearest "official time-point".
 * </p>
 * <p>
 * The timeline also supports a delimiting history-length which makes it
 * possible to forget data after some time.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 */
public class StatisticsHistory extends History<Statistics> {
	/** The unique class ID */
	private static final long serialVersionUID = 138616216198514575L;

	/* A list of series-keys, each one denoting a dimension of the data-vectors */
	LinkedList<String> seriesKeys = new LinkedList<String>();

	/**
	 * Creates a time-line which will forget about data after
	 * <code>history</code> milliseconds.
	 * 
	 * @param stepSize
	 *            The time-gap between time-points.
	 * @param history
	 *            The history length.
	 */
	public StatisticsHistory(long t, long history) {
		super(t, history);
	}

	public StatisticsHistory(StatisticsHistory h) {
		super(h.stepSize, h.historyLength);
		this.seriesKeys.addAll(h.seriesKeys);
		this.map.putAll(h.map);
	}

	public LinkedList<String> getSeriesKeys() {
		return seriesKeys;
	}

	public void add(Long time, Statistics newStats) {

		Statistics stats = new Statistics(newStats);

		for (String key : newStats.keySet())
			if (!this.seriesKeys.contains(key)) {
				seriesKeys.add(key);
			}

		Statistics existing = super.get(time);
		if (existing != null) {
			synchronized (existing) {
				existing.add(stats);
			}
		} else {
			Long tk = this.adjust(time);
			synchronized (map) {
				map.put(tk, stats);
			}
		}

		// else
		// super.add( time, stats );

		last = Math.max(last, time);
		this.forget();
	}

	public Date getStart() {
		return new Date(this.last() - this.getLength());
	}

	public Date getEnd() {
		return new Date(last()); // this.getTimestamp( 0 ) + this.getLength() );
	}

	public String toString() {
		StringBuffer s = new StringBuffer();

		for (int i = 0; i < this.getSteps(); i++) {
			s.append("  " + new Date(getTimestamp(i)) + "  ~>  " + this.map(i)
					+ "  =>  " + get(i) + "\n");
		}

		s.append("----------------\n");
		for (Long key : this.map.keySet())
			s.append(key + "  ~>  " + map.get(key) + "\n");

		s.append("\n Timeline has " + this.map.size() + " entries.");
		s.append("\nIt starts at " + this.getStart() + " and ends up at "
				+ this.getEnd());
		return s.toString();
	}

	public void moveTo(long time) {
		this.add(time, new Statistics(""));
		forget();
	}

	/**
	 * This method will sum up all statistics found in this timeline instance
	 * into an "aggregated statistics" object.
	 * 
	 * @return A new Statistics instance, containing sums over all series.
	 */
	public Statistics fold() {

		Statistics folded = new Statistics("Total");

		for (Statistics data : this.map.values())
			folded.add(data);

		return folded;
	}

	public Object readResolve() {
		// log.debug( "readResolve()... seriesKeys: {}", this.seriesKeys );
		if (seriesKeys == null)
			seriesKeys = new LinkedList<String>();

		if (this.map != null) {
			// log.debug( "Checking series-keys..." );
			for (Statistics st : this.map.values()) {
				for (String key : st.keySet())
					if (!seriesKeys.contains(key)) {
						// log.debug( "Adding series-key '{}'", key );
						seriesKeys.add(key);
					}
			}
		}

		return this;
	}

	public List<Statistics> getData() {
		List<Statistics> list = new ArrayList<Statistics>();
		for (Long key : map.keySet()) {
			Statistics st = new Statistics();
			st.add("@timestamp", new Double(key));
			st.add(map.get(key));
			list.add(st);
		}
		return list;
	}
}