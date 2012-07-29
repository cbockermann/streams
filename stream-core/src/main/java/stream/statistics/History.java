/*
 *  streams library
 *
 *  Copyright (C) 2011-2012 by Christian Bockermann, Hendrik Blom
 * 
 *  streams is a library, API and runtime environment for processing high
 *  volume data streams. It is composed of three submodules "stream-api",
 *  "stream-core" and "stream-runtime".
 *
 *  The streams library (and its submodules) is free software: you can 
 *  redistribute it and/or modify it under the terms of the 
 *  GNU Affero General Public License as published by the Free Software 
 *  Foundation, either version 3 of the License, or (at your option) any 
 *  later version.
 *
 *  The stream.ai library (and its submodules) is distributed in the hope
 *  that it will be useful, but WITHOUT ANY WARRANTY; without even the implied 
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/.
 */
package stream.statistics;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>
 * This is a generic implementation of a history. Basically a history associates
 * items of type <code>T</code> with timestamps, which are grouped to discrete
 * timestamps, e.g. every full hour, every 5 minutes, etc.
 * </p>
 * <p>
 * The type <code>T</code> is usually a data structure, which allows aggregation
 * of multiple values of nearby time points.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 * 
 * @param <T>
 *            The type of objects associated with each timepoint/timestamp.
 */
public class History<T> implements Serializable {

	/** The unique class ID */
	private static final long serialVersionUID = 9079427772146519028L;

	public final static long MILLISECOND = 1L;

	/* A time constant for seconds */
	public final static long SECOND = 1000L;

	/* A constant for minutes */
	public final static long MINUTE = 60 * SECOND;

	/* A constant for hours */
	public final static long HOUR = 60 * MINUTE;

	/* A constant for days */
	public final static long DAY = 24 * HOUR;

	/* A constant for weeks */
	public final static long WEEK = 7 * DAY;

	/* A constant for months */
	public final static long MONTH = 30 * DAY;

	/* A constant for years */
	public final static long YEAR = 52 * WEEK;

	long stepSize;

	long historyLength;

	LinkedHashMap<Long, T> map = new LinkedHashMap<Long, T>();

	long last = 0L;

	public History(long stepSize, long historyLength) {
		this.stepSize = stepSize;
		this.historyLength = historyLength;
	}

	/**
	 * Returns the step size for this history.
	 * 
	 * @return
	 */
	public long getStepSize() {
		return this.stepSize;
	}

	/**
	 * @see org.jwall.web.audit.console.statistics.History#getLength()
	 */
	public Long getLength() {
		return historyLength;
	}

	/**
	 * @see org.jwall.web.audit.console.statistics.History#get(int)
	 */
	public T get(int i) {
		Long key = map(i);
		return map.get(key);
	}

	/**
	 * @see org.jwall.web.audit.console.statistics.History#getPosition(java.lang.Long)
	 */
	public int getPosition(Long timestamp) {
		LinkedList<Long> times = new LinkedList<Long>(map.keySet());
		return times.indexOf(timestamp);
	}

	/**
	 * @see org.jwall.web.audit.console.statistics.History#getSteps()
	 */
	public int getSteps() {
		Long steps = new Long(this.getLength() / this.getStepSize());
		return steps.intValue();
	}

	/**
	 * @see org.jwall.web.audit.console.statistics.History#getTimestamp(int)
	 */
	public Long getTimestamp(int i) {
		long first = last() - this.getLength();
		long t = first + i * stepSize;
		return adjust(t);
	}

	/**
	 * @see org.jwall.web.audit.console.statistics.History#get(java.lang.Long)
	 */
	public T get(Long timestamp) {
		return map.get(adjust(timestamp));
	}

	/**
	 * @see org.jwall.web.audit.console.statistics.History#add(java.lang.Long,
	 *      java.lang.Object)
	 */
	public void add(Long timestamp, T data) {
		Long time = adjust(timestamp);
		map.put(time, data);
		last = Math.max(last, time);
	}

	/**
	 * This method maps the given timestamp to the last <i>official</i>
	 * timestamp, as induced by the stepSize.
	 * 
	 * @param timestamp
	 * @return
	 */
	protected Long adjust(Long timestamp) {
		long rest = timestamp % stepSize;
		return timestamp - rest;
	}

	public Long mapTimestamp(Long timestamp) {
		return adjust(timestamp);
	}

	/**
	 * This method removes any data that is associated with a timestamp older
	 * than <code>historyLength</code> milliseconds. Items will only be removed
	 * if older than <code>historyLength</code> <b>AND</b> there are more than
	 * <code>getSteps()</code> items contained in this history.
	 * 
	 */
	public void forget() {

		if (map.size() < getSteps())
			return;

		// long limit = System.currentTimeMillis() - this.historyLength;
		long limit = last - this.historyLength;
		List<Long> delete = new LinkedList<Long>();

		for (Long key : map.keySet()) {
			if (key.compareTo(limit) < 0)
				delete.add(key);
		}

		for (Long key : delete)
			map.remove(key);
	}

	/**
	 * Returns the last <i>official</i> timestamp for this history. This is
	 * either the timestamp of the last element added to the history or the
	 * adjusted current timestamp.
	 * 
	 * @return
	 */
	protected long last() {
		return last;
		// last = Math.max( last, adjust( System.currentTimeMillis() ) );
		// return last;
	}

	/**
	 * Returns the timestamp for the <i>i</i>th element of this history.
	 * 
	 * @param i
	 * @return
	 */
	public Long map(int i) {
		long first = last() - this.getLength();
		long t = first + i * stepSize;
		return adjust(t);
	}

	public List<T> getData() {
		List<T> list = new ArrayList<T>();
		for (Long key : map.keySet()) {
			list.add(map.get(key));
		}
		return list;
	}

	public void clear() {
		this.last = 0L;
		this.map.clear();
	}
}