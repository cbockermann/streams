/**
 * 
 */
package stream.counter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * A simple unbounded counter that manages elements and their frequency counts
 * in a large hash map.
 * </p>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public class ExactCounter<T extends Serializable> implements Counter<T> {

	/** The unique class ID */
	private static final long serialVersionUID = -2314843542757616354L;

	static Logger log = LoggerFactory.getLogger(ExactCounter.class);

	final AtomicLong total = new AtomicLong(0L);
	final Map<T, AtomicLong> counts = new HashMap<T, AtomicLong>();

	/**
	 * @see stream.counter.Counter#getTotalCount()
	 */
	@Override
	public Long getTotalCount() {
		return total.longValue();
	}

	/**
	 * @see stream.counter.Counter#keySet()
	 */
	@Override
	public Set<T> keySet() {
		return Collections.unmodifiableSet(counts.keySet());
	}

	/**
	 * @see stream.counter.Counter#count(java.lang.Object)
	 */
	@Override
	public void count(T element) {

		AtomicLong cnt = counts.get(element);
		if (cnt == null) {
			cnt = new AtomicLong(0L);
			counts.put(element, cnt);
		}

		cnt.incrementAndGet();
	}

	/**
	 * @see stream.counter.Counter#getCount(java.lang.Object)
	 */
	@Override
	public Long getCount(T element) {

		AtomicLong cnt = counts.get(element);
		if (cnt == null)
			return 0L;

		return cnt.get();
	}

	/**
	 * <p>
	 * This method trancates the size of this distribution-model by removing
	 * elements until only the given maximum number of elements resides in the
	 * count-map.
	 * </p>
	 * <p>
	 * Specifying any value &lt 1 for <code>maxElements</code> will completely
	 * prune all elements.
	 * </p>
	 * 
	 * @param maxElements
	 */
	public void truncate(int maxElements) {
		log.trace("Truncating distribution to {} elements", maxElements);
		if (maxElements < 1) {
			total.set(0L);
			counts.clear();
			return;
		}

		synchronized (counts) {
			List<T> elements = new ArrayList<T>(counts.keySet());
			Collections.sort(elements, new DistributionComparator(counts));

			log.trace("Sorted elements: {}", elements);
			int removed = 0;
			for (int i = 0; counts.size() > maxElements; i++) {
				AtomicLong cnt = counts.remove(elements.get(i));
				total.set(total.get() - cnt.get());
				removed++;
			}
			log.debug("removed {} elements", removed);
		}
	}

	/**
	 * This comparator can be used for sorting elements in ascending order,
	 * based on their frequencies.
	 */
	class DistributionComparator implements Comparator<T> {
		Map<T, AtomicLong> counts;

		public DistributionComparator(Map<T, AtomicLong> counts) {
			this.counts = counts;
		}

		/**
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(T arg0, T arg1) {
			Integer i0 = 0;
			Integer i1 = 0;
			AtomicLong c0 = counts.get(arg0);
			if (c0 != null)
				i0 = c0.intValue();

			AtomicLong c1 = counts.get(arg1);
			if (c1 != null)
				i1 = c1.intValue();

			int rc = i0.compareTo(i1);
			if (rc == 0) {
				return arg0.toString().compareTo(arg1.toString());
			}

			return rc;
		}
	}
}