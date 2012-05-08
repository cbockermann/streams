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
package stream.data;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import stream.Processor;
import stream.annotations.Description;

/**
 * @author chris
 * 
 */
@Description(group = "Data Stream.Processing.Transformations.Data")
public class NumericalBinning implements Processor {

	Double minimum = 0.0d;

	Double maximum = 10.0d;

	Integer bins = 10;

	String include = ".*";

	Bucket[] buckets = null;

	/**
	 * @return the minimum
	 */
	public Double getMinimum() {
		return minimum;
	}

	/**
	 * @param minimum
	 *            the minimum to set
	 */
	public void setMinimum(Double minimum) {
		this.minimum = minimum;
	}

	/**
	 * @return the maximum
	 */
	public Double getMaximum() {
		return maximum;
	}

	/**
	 * @param maximum
	 *            the maximum to set
	 */
	public void setMaximum(Double maximum) {
		this.maximum = maximum;
	}

	/**
	 * @return the bins
	 */
	public Integer getBins() {
		return bins;
	}

	/**
	 * @param bins
	 *            the bins to set
	 */
	public void setBins(Integer bins) {
		this.bins = bins;
		buckets = null;
	}

	/**
	 * @return the include
	 */
	public String getInclude() {
		return include;
	}

	/**
	 * @param include
	 *            the include to set
	 */
	public void setInclude(String include) {
		this.include = include;
	}

	/**
	 * @see stream.AbstractProcessor#init()
	 */
	public void init() throws Exception {
		buckets = new Bucket[Math.max(1, bins)];
		double step = (maximum - minimum) / bins.doubleValue();
		double last = minimum;
		for (int i = 0; i < buckets.length - 1; i++) {
			buckets[i] = new Bucket(last, last + step);
			last += step;
		}
		buckets[buckets.length - 1] = new Bucket(last, maximum);
	}

	/**
	 * @see stream.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data data) {

		if (buckets == null) {
			try {
				init();
			} catch (Exception e) {
				throw new RuntimeException("Initialization failed: "
						+ e.getMessage());
			}
		}

		for (String key : DataUtils.getKeys(data)) {
			if ((include == null || key.matches(include))
					&& data.get(key).getClass() == Double.class)
				data.put(key, map((Double) data.get(key)));
		}

		return data;
	}

	protected Bucket map(Double d) {
		if (d < buckets[0].upper)
			return buckets[0];

		for (int i = 0; i < buckets.length; i++)
			if (i + 1 < buckets.length && buckets[i + 1].lower > d)
				return buckets[i];

		return buckets[buckets.length - 1];
	}

	public class Bucket implements Serializable, Comparable<Bucket> {
		/** The unique class ID */
		private static final long serialVersionUID = 1874196246174345683L;
		final Double lower;
		final Double upper;
		final String asString;

		public Bucket(double low, double high) {
			lower = low;
			upper = high;
			DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(
					Locale.getDefault());
			otherSymbols.setDecimalSeparator('.');
			DecimalFormat fmt = new DecimalFormat("0.0#####", otherSymbols);
			asString = "Range[" + fmt.format(lower) + ";" + fmt.format(upper)
					+ "]";
		}

		public String toString() {
			return asString;
		}

		/**
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(Bucket arg0) {
			if (arg0 == null)
				return 1;

			if (arg0 == this)
				return 0;

			int r = this.lower.compareTo(arg0.lower);
			if (r == 0) {
				r = this.upper.compareTo(arg0.upper);
			}

			return r;
		}
	}
}