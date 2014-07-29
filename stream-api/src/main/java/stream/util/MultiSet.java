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
package stream.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author chris
 * 
 */
public class MultiSet<T> implements Set<T> {

	final Map<T, AtomicLong> countMap = new LinkedHashMap<T, AtomicLong>();

	/**
	 * @see java.util.Set#add(java.lang.Object)
	 */
	@Override
	public boolean add(T arg0) {
		if (countMap.containsKey(arg0)) {
			countMap.get(arg0).incrementAndGet();
			return true;
		} else {
			countMap.put(arg0, new AtomicLong(1L));
			return false;
		}
	}

	/**
	 * @see java.util.Set#addAll(java.util.Collection)
	 */
	@Override
	public boolean addAll(Collection<? extends T> arg0) {
		boolean added = false;
		for (T obj : arg0) {
			added = add(obj) | added;
		}
		return added;
	}

	/**
	 * @see java.util.Set#clear()
	 */
	@Override
	public void clear() {
		countMap.clear();
	}

	/**
	 * @see java.util.Set#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(Object arg0) {
		return countMap.containsKey(arg0);
	}

	/**
	 * @see java.util.Set#containsAll(java.util.Collection)
	 */
	@Override
	public boolean containsAll(Collection<?> arg0) {

		for (Object o : arg0) {
			if (!countMap.containsKey(o))
				return false;
		}

		return true;
	}

	/**
	 * @see java.util.Set#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return countMap.isEmpty();
	}

	/**
	 * @see java.util.Set#iterator()
	 */
	@Override
	public Iterator<T> iterator() {
		return countMap.keySet().iterator();
	}

	/**
	 * @see java.util.Set#remove(java.lang.Object)
	 */
	@Override
	public boolean remove(Object arg0) {

		if (countMap.containsKey(arg0)) {
			long cnt = countMap.get(arg0).decrementAndGet();
			if (cnt < 1)
				countMap.remove(arg0);
			return true;
		}

		return false;
	}

	/**
	 * @see java.util.Set#removeAll(java.util.Collection)
	 */
	@Override
	public boolean removeAll(Collection<?> arg0) {
		boolean removed = false;

		for (Object o : arg0) {
			removed = remove(o) | removed;
		}

		return removed;
	}

	/**
	 * @see java.util.Set#retainAll(java.util.Collection)
	 */
	@Override
	public boolean retainAll(Collection<?> arg0) {
		boolean removed = false;
		Iterator<T> it = countMap.keySet().iterator();
		while (it.hasNext()) {
			T obj = it.next();
			if (!arg0.contains(obj)) {
				it.remove();
				removed = true;
			}
		}

		return removed;
	}

	/**
	 * @see java.util.Set#size()
	 */
	@Override
	public int size() {
		return countMap.size();
	}

	/**
	 * @see java.util.Set#toArray()
	 */
	@Override
	public Object[] toArray() {
		int i = 0;
		Object[] result = new Object[countMap.size()];
		for (Object obj : countMap.keySet()) {
			result[i] = obj;
			i++;
		}

		return result;
	}

	/**
	 */
	@SuppressWarnings({ "unchecked", "hiding" })
	@Override
	public <T> T[] toArray(T[] arg0) {

		Iterator<?> it = countMap.keySet().iterator();
		for (int i = 0; i < arg0.length && i < countMap.size(); i++) {
			T obj = (T) it.next();
			arg0[i] = obj;
		}

		return arg0;
	}

	public int count(T arg0) {
		if (countMap.containsKey(arg0))
			return countMap.get(arg0).intValue();
		return 0;
	}
}