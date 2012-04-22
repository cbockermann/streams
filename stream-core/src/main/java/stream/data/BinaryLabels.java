/*
 *  stream.ai
 *
 *  Copyright (C) 2011-2012 by Christian Bockermann, Hendrik Blom
 * 
 *  stream.ai is a library, API and runtime environment for processing high
 *  volume data streams. It is composed of three submodules "stream-api",
 *  "stream-core" and "stream-runtime".
 *
 *  The stream.ai library (and its submodules) is free software: you can 
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

import stream.AbstractProcessor;

/**
 * This class implements a simple strategy to map labels to { -1, +1 }
 * 
 * @author chris@jwall.org
 * 
 */
public class BinaryLabels extends AbstractProcessor {

	String labelAttribute;
	String positive = null;
	Double threshold = null;

	public BinaryLabels() {
		this(null, null);
	}

	public BinaryLabels(String label) {
		this(label, null);
	}

	public BinaryLabels(String label, String positive) {
		this.labelAttribute = label;
		this.positive = positive;
	}

	/**
	 * @return the threshold
	 */
	public Double getThreshold() {
		return threshold;
	}

	/**
	 * @param threshold
	 *            the threshold to set
	 */
	public void setThreshold(Double threshold) {
		this.threshold = threshold;
	}

	/**
	 * @return the key
	 */
	public String getLabel() {
		return labelAttribute;
	}

	/**
	 * @param key
	 *            the key to set
	 */
	public void setLabel(String key) {
		this.labelAttribute = key;
	}

	/**
	 * @return the positive
	 */
	public String getPositive() {
		return positive;
	}

	/**
	 * @param positive
	 *            the positive to set
	 */
	public void setPositive(String positive) {
		this.positive = positive;
	}

	@Override
	public Data process(Data data) {
		if (labelAttribute == null) {
			for (String k : data.keySet()) {
				if (k.startsWith("@label")) {
					labelAttribute = k;
					break;
				}
			}
		}

		if (labelAttribute == null)
			return data;

		Serializable val = data.get(labelAttribute);
		if (val == null)
			return data;

		if (val instanceof Double) {
			//
			// handle numerical values by threshold
			//
			if (threshold == null)
				threshold = 0.0d;

			Double d = (Double) val;
			if (d < threshold) {
				data.put(labelAttribute, -1.0d);
			} else
				data.put(labelAttribute, 1.0d);
			return data;

		} else {
			//
			// handle nominal values by checking against the
			// defined positive value.
			//
			if (positive == null)
				positive = val.toString();

			if (positive.equals(val))
				data.put(labelAttribute, 1.0d);
			else
				data.put(labelAttribute, -1.0d);

			return data;
		}
	}
}