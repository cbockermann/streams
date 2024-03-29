/*
 *  streams library
 *
 *  Copyright (C) 2011-2014 by Christian Bockermann, Hendrik Blom
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
package stream.data.vector;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import stream.Data;
import stream.Measurable;
import stream.data.DataUtils;

/**
 * @author Sangkyun Lee
 * @deprecated
 */
public class Vector implements Serializable, Measurable {

	private static final long serialVersionUID = -5547469461526949533L;

	public enum Type {
		DENSE, SPARSE
	};

	Type type;
	double scale = 1.0d;
	double snorm = 0.0d;
	// For type==SPARSE
	HashMap<Integer, Double> pairs;
	int max_index = -1;
	// For type==DENSE
	double[] vals;
	int length = -1;

	public Type getType() {
		return type;
	}

	public boolean isSparse() {
		return type == Type.SPARSE;
	}

	public HashMap<Integer, Double> getPairs() {
		return pairs;
	}

	/**
	 * Creates a sparse vector.
	 */
	public Vector() {
		type = Type.SPARSE;
		pairs = new HashMap<Integer, Double>();
		max_index = 0;
	}

	/**
	 * Creates a dense vector of the specified length.
	 * 
	 * @param length
	 */
	public Vector(int length) {
		type = Type.DENSE;
		this.length = length;
		vals = new double[length];
	}

	/**
	 * Creates a sparse vector from the given indices and values.
	 * 
	 * @param index
	 * @param vals
	 */
	public Vector(int[] index, double[] vals) {
		type = Type.SPARSE;
		length = index.length;
		pairs = new HashMap<Integer, Double>();
		for (int i = 0; i < length; ++i) {
			pairs.put(index[i], vals[i]);
			snorm += vals[i] * vals[i];
			if (index[i] > max_index)
				max_index = index[i];
		}
	}

	/**
	 * Creates a dense vector from the given values.
	 * 
	 * @param vals
	 */
	public Vector(double[] vals) {
		this(vals, true);
	}

	public Vector(double[] vals, boolean copy) {
		type = Type.DENSE;
		length = vals.length;
		if (!copy) {
			this.vals = vals;
			for (int i = 0; i < length; ++i) {
				snorm += vals[i] * vals[i];
			}
		} else {
			this.vals = new double[length];

			for (int i = 0; i < length; ++i) {
				this.vals[i] = vals[i];
				snorm += vals[i] * vals[i];
			}
		}
	}

	/**
	 * Creates a sparse vector from a HashMap
	 * 
	 * @param pairs
	 * @param copy
	 */
	public Vector(HashMap<Integer, Double> pairs) {
		this(pairs, true);
	}

	public Vector(HashMap<Integer, Double> pairs, boolean copy) {
		type = Type.SPARSE;
		if (!copy) {
			this.pairs = pairs;
			for (Map.Entry<Integer, Double> entry : pairs.entrySet()) {
				int idx = entry.getKey();
				double val = entry.getValue();
				snorm += val * val;
				if (idx > max_index)
					max_index = idx;
			}
		} else {
			this.pairs = new HashMap<Integer, Double>();
			for (Map.Entry<Integer, Double> entry : pairs.entrySet()) {
				int idx = entry.getKey();
				double val = entry.getValue();
				snorm += val * val;
				if (idx > max_index)
					max_index = idx;
				this.pairs.put(idx, val);
			}
		}
	}

	/**
	 * A copy constructor
	 */
	public Vector(Vector x) {
		type = x.type;

		if (x.type == Type.DENSE) {
			length = x.length;
			this.vals = new double[length];

			for (int i = 0; i < length; ++i) {
				vals[i] = x.vals[i];
				snorm += vals[i] * vals[i];
			}
		} else {
			this.pairs = new HashMap<Integer, Double>();
			for (Map.Entry<Integer, Double> xentry : x.getPairs().entrySet()) {
				int idx = xentry.getKey();
				double val = xentry.getValue();
				snorm += val * val;
				if (idx > max_index)
					max_index = idx;
				this.pairs.put(idx, val);
			}
		}
	}

	/**
	 * Creates a dense vector filling the values in the given indices of
	 * non-zero entries.
	 * 
	 * @param index
	 * @param vals
	 * @param dimension
	 *            ;
	 */
	public Vector(int[] index, double[] vals, int length) {
		type = Type.DENSE;
		this.length = length;
		this.vals = new double[length];
		for (int i = 0; i < index.length; ++i) {
			int idx = index[i];
			this.vals[idx] = vals[i];
			snorm += vals[i] * vals[i];
		}
	}

	public int length() {
		if (type == Type.SPARSE)
			return (max_index + 1);
		else
			return length;
	}

	public void set(int j, double d) {
		double val = d / scale;
		Double my_val;
		if (type == Type.SPARSE) {
			my_val = this.pairs.get(j);
			if (my_val != null)
				snorm -= my_val * my_val;
			snorm += val * val;
			this.pairs.put(j, val);
			if (j > max_index)
				max_index = j;
		} else {
			if (j < length) {
				my_val = vals[j];
				snorm += -my_val * my_val + val * val;
			}
			snorm += val * val;
			vals[j] = val;
		}
	}

	public double get(int i) {
		if (type == Type.SPARSE)
			return scale * pairs.get(i);
		else
			return scale * vals[i];
	}

	public Vector scale(double s) {
		snorm *= (s * s);
		if (s != 0.0)
			scale *= s;
		else {
			scale = 1.0d;
			if (type == Type.SPARSE) {
				for (Map.Entry<Integer, Double> entry : pairs.entrySet())
					entry.setValue(0.0d);
			} else {
				for (int i = 0; i < length; ++i)
					vals[i] = 0.0d;
			}
		}
		return this;
	}

	public Vector add(Vector x) {
		return add(1.0d, x);
	}

	public Vector add(double factor, Vector x) {
		double xterm = 0.0, snorm_x = 0.0;

		if (type == Type.SPARSE && x.type == Type.SPARSE) {
			for (Map.Entry<Integer, Double> entry : x.pairs.entrySet()) {
				Integer idx = entry.getKey();
				Double x_val = factor * x.scale * entry.getValue();
				snorm_x += x_val * x_val;
				Double my_val = this.pairs.get(idx);
				if (my_val != null) {
					xterm += 2.0 * my_val * x_val;
					this.pairs.put(idx, my_val + x_val / scale);
				} else {
					this.pairs.put(idx, x_val / scale);
				}
				if (idx > max_index)
					max_index = idx;
			}
		} else if (type == Type.DENSE && x.type == Type.SPARSE) {
			for (Map.Entry<Integer, Double> entry : x.pairs.entrySet()) {
				Integer idx = entry.getKey();
				Double x_val = factor * x.scale * entry.getValue();
				snorm_x += x_val * x_val;
				double my_val = vals[idx];
				xterm += 2.0 * my_val * x_val;
				vals[idx] = my_val + x_val / scale;
			}
		} else if (type == Type.SPARSE && x.type == Type.DENSE) {
			for (int i = 0; i < x.length; ++i) {
				double x_val = factor * x.scale * x.vals[i];
				snorm_x += x_val * x_val;
				Double my_val = this.pairs.get(i);
				if (my_val != null) {
					xterm += 2.0 * my_val * x_val;
					this.pairs.put(i, my_val + x_val / scale);
				} else {
					this.pairs.put(i, x_val / scale);
				}
			}
			max_index = x.length - 1;
		} else { // both dense
			for (int i = 0; i < x.length; ++i) {
				double x_val = factor * x.scale * x.vals[i];
				snorm_x += x_val * x_val;
				double my_val = vals[i];
				xterm += 2.0 * my_val * x_val;
				vals[i] = my_val + x_val / scale;
			}
		}
		this.snorm += snorm_x + this.scale * xterm;

		return this;
	}

	public double norm() {
		return Math.sqrt(snorm());
	}

	public double snorm() {
		return snorm;
	}

	public double innerProduct(Vector x) {
		double sum = 0.0d;

		if (type == Type.SPARSE && x.type == Type.SPARSE) {
			for (Map.Entry<Integer, Double> entry : x.pairs.entrySet()) {
				Integer idx = entry.getKey();
				Double x_val = entry.getValue();
				Double my_val = this.pairs.get(idx);
				if (my_val != null)
					sum += my_val * x_val;
			}
		} else if (type == Type.DENSE && x.type == Type.SPARSE) {
			for (Map.Entry<Integer, Double> entry : x.pairs.entrySet()) {
				Integer idx = entry.getKey();
				Double x_val = entry.getValue();
				double my_val = vals[idx];
				sum += my_val * x_val;
			}
		} else if (type == Type.SPARSE && x.type == Type.DENSE) {
			for (Map.Entry<Integer, Double> entry : this.pairs.entrySet()) {
				Integer idx = entry.getKey();
				Double my_val = entry.getValue();
				double x_val = x.vals[idx];
				sum += my_val * x_val;
			}
		} else { // both are DENSE
			for (int i = 0; i < length; ++i) {
				sum += vals[i] * x.vals[i];
			}
		}
		sum *= scale * x.scale;

		return sum;
	}

	@Override
	public String toString() {
		StringBuffer s = new StringBuffer();
		if (type == Type.SPARSE) {
			for (Map.Entry<Integer, Double> entry : pairs.entrySet()) {
				if (entry.getValue() > 0.0d) {
					s.append(" ");
					s.append(entry.getKey());
					s.append(":");
					s.append(scale * entry.getValue());
				}
			}
		} else {
			for (int i = 0; i < length; ++i) {
				if (vals[i] > 0.0d) {
					s.append(" ");
					s.append(i);
					s.append(":");
					s.append(scale * vals[i]);
				}
			}
		}
		return s.toString();
	}

	@Override
	public double getByteSize() {
		// here we add up the sizes of all things we'er storing
		// in this vector

		//
		// 2 * 8 bytes (64 bit index, 64 bit double value)
		//
		double size = pairs.size() * 16.0d;

		// the scaler
		size += 8.0d;

		// the cached norm
		size += 8.0d;

		return size;
	}

	public static Vector createSparseVector(Data datum) {
		if (datum.containsKey(Data.ANNOTATION_PREFIX + "__sparse-vector")) {
			return (InputVector) datum.get(Data.ANNOTATION_PREFIX
					+ "__sparse-vector");
		}

		for (Serializable val : datum.values()) {
			if (val instanceof InputVector) {
				return (InputVector) val;
			}
		}

		TreeSet<String> indexes = new TreeSet<String>();
		for (String key : datum.keySet()) {
			Serializable val = datum.get(key);
			if (!DataUtils.isAnnotation(key) && key.matches("\\d+")
					&& Number.class.isAssignableFrom(val.getClass())) {
				indexes.add(key);
			}
		}

		double y = Double.NaN;
		if (datum.containsKey("@label")) {
			try {
				y = (Double) datum.get("@label");
			} catch (Exception e) {
				y = Double.NaN;
			}
		}

		// int[] idx = new int[ indexes.size() ];
		// double[] vals = new double[ indexes.size() ];
		HashMap<Integer, Double> pairs = new HashMap<Integer, Double>();

		// int i = 0;
		for (String key : indexes) {
			// idx[i] = Integer.parseInt( key );
			// vals[i] = (Double) datum.get( key );
			// i++;
			pairs.put((Integer) Integer.parseInt(key), (Double) datum.get(key));
		}

		// SparseVector vec = new SparseVector( idx, vals, y, false );
		InputVector vec = new InputVector(pairs, false, y);
		return vec;
	}
}