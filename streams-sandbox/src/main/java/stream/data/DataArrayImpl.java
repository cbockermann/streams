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
package stream.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import stream.Data;

/**
 * <p>
 * This implementation of a Data item is backed by two arrays. A string array
 * holding the keys and an array of Serializable objects representing the
 * values.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 * @deprecated
 */
public class DataArrayImpl implements Data {

	/** The unique class ID */
	private static final long serialVersionUID = -1633697890159204967L;

	protected int ext = 10;
	protected String[] keys = new String[10];
	protected Serializable[] values = new Serializable[10];
	int limit = 10;
	int size = 0;

	/**
	 * @see java.util.Map#clear()
	 */
	@Override
	public void clear() {
		for (int i = 0; i < keys.length; i++) {
			keys[i] = null;
			values[i] = null;
		}
		limit = 0;
	}

	protected int indexOf(String key) {
		if (key == null)
			return -1;

		for (int i = 0; i < limit && i < keys.length; i++) {
			if (key.equals(keys[i]))
				return i;
		}
		return -1;
	}

	/**
	 * @see java.util.Map#containsKey(java.lang.Object)
	 */
	@Override
	public boolean containsKey(Object key) {
		return indexOf(key.toString()) > 0;
	}

	/**
	 * @see java.util.Map#containsValue(java.lang.Object)
	 */
	@Override
	public boolean containsValue(Object value) {

		if (value == null)
			return false;

		for (int i = 0; i < limit && i < values.length; i++) {
			if (value.equals(values[i]))
				return true;
		}

		return false;
	}

	/**
	 * @see java.util.Map#entrySet()
	 */
	@Override
	public Set<java.util.Map.Entry<String, Serializable>> entrySet() {

		Set<java.util.Map.Entry<String, Serializable>> entries = new LinkedHashSet<java.util.Map.Entry<String, Serializable>>();

		for (int i = 0; i < limit && i < keys.length; i++) {
			entries.add(new Pair(keys[i], values[i]));
		}

		return entries;
	}

	/**
	 * @see java.util.Map#get(java.lang.Object)
	 */
	@Override
	public Serializable get(Object key) {

		if (key == null)
			return null;

		int idx = indexOf(key.toString());
		if (idx >= 0 && idx < limit)
			return values[idx];
		return null;
	}

	/**
	 * @see java.util.Map#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return limit == 0;
	}

	/**
	 * @see java.util.Map#keySet()
	 */
	@Override
	public Set<String> keySet() {
		Set<String> ks = new LinkedHashSet<String>();
		for (int i = 0; i < limit && i < keys.length; i++) {
			if (keys[i] != null)
				ks.add(keys[i]);
		}

		return ks;
	}

	/**
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	@Override
	public Serializable put(String key, Serializable value) {

		int idx = indexOf(key);
		if (idx >= 0) {
			values[idx] = value;
			return values[idx];
		}

		if (limit < keys.length) {
			keys[limit] = key;
			values[limit] = value;
			limit++;
			size++;
			return value;
		}

		String[] nk = new String[keys.length + ext];
		Serializable[] nv = new Serializable[keys.length + ext];

		for (int i = 0; i < limit; i++) {
			nk[i] = keys[i];
			nv[i] = values[i];
		}

		nk[limit] = key;
		nv[limit] = value;
		limit++;
		size++;

		return value;
	}

	/**
	 * @see java.util.Map#putAll(java.util.Map)
	 */
	@Override
	public void putAll(Map<? extends String, ? extends Serializable> m) {
		for (String key : m.keySet()) {
			put(key, m.get(key));
		}
	}

	/**
	 * @see java.util.Map#remove(java.lang.Object)
	 */
	@Override
	public Serializable remove(Object key) {

		int idx = indexOf(key.toString());
		if (idx > 0) {
			keys[idx] = null;
			Serializable value = values[idx];
			values[idx] = null;
			size--;
			return value;
		}

		return null;
	}

	/**
	 * @see java.util.Map#size()
	 */
	@Override
	public int size() {
		return size;
	}

	/**
	 * @see java.util.Map#values()
	 */
	@Override
	public Collection<Serializable> values() {
		List<Serializable> vals = new ArrayList<Serializable>(size);
		for (int i = 0; i < limit && i < values.length; i++) {
			if (values[i] != null)
				vals.add(values[i]);
		}
		return vals;
	}

	/**
	 * @see stream.Data#createCopy()
	 */
	@Override
	public Data createCopy() {
		return DataFactory.copy(this);
	}

	public final class Pair implements
			java.util.Map.Entry<String, Serializable>, Serializable {

		/** The unique class ID */
		private static final long serialVersionUID = 6908846231903505137L;

		String key;
		Serializable value;

		public Pair(String key, Serializable value) {
			this.key = key;
			this.value = value;
		}

		/**
		 * @see java.util.Map.Entry#getKey()
		 */
		@Override
		public String getKey() {
			return key;
		}

		/**
		 * @see java.util.Map.Entry#getValue()
		 */
		@Override
		public Serializable getValue() {
			return value;
		}

		/**
		 * @see java.util.Map.Entry#setValue(java.lang.Object)
		 */
		@Override
		public Serializable setValue(Serializable arg0) {
			this.value = arg0;
			return value;
		}
	}
}
