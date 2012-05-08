package stream.mining.counter;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.util.SizeOf;

/**
 * 
 * This is a simple implementation of a stream-counting model. The model is
 * updatable and will - for a given threshold <code>k</code> - approximate the
 * counts of the top-k elements within a example-set/stream.
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public class TopKCounter implements Comparator<Serializable> {
	static Logger log = LoggerFactory.getLogger(TopKCounter.class.getName());

	/** This map holds the list of monitored items and their counters */
	final HashMap<Serializable, Long> topK = new HashMap<Serializable, Long>();

	/**
	 * This is the maximum number of observed items within the
	 * stream/example-set
	 */
	int k;

	/** The number of elements that have been processed */
	Long cnt = 0L;

	/**
	 * This initially creates a counting model for streams. The model will not
	 * use more than the last <code>k</code> elements in order to approximate
	 * the item counts within the stream.
	 * 
	 * @param examples
	 *            The initial example set. Not that this will usually consist of
	 *            a one-element example set.
	 * @param k
	 *            The maximum number of items that may be tracked/monitored by
	 *            the model.
	 */
	public TopKCounter(int k) {
		this.k = k;
		log.debug("Creating top-k counter with k = {}", k);
		init();
	}

	/**
	 * @see stream.learner.Learner#init()
	 */
	public void init() {
		cnt = 0L;
		topK.clear();
	}

	/**
	 * This method actually does all the work when learning from the stream. It
	 * will update the inner structures to reflect the incoporation of the given
	 * example.
	 * 
	 * @param ex
	 */
	public void learn(Serializable example) {
		cnt++;
		if (cnt % 100 == 0)
			log.debug("   space used: {}/{}", topK.size(), k);

		// is the element already in the list of our top-k monitored items?
		//
		synchronized (topK) {
			if (topK.get(example) != null) {

				log.debug("Incrementing count of top-k element {}", example);
				// LogService.getGlobal().logNote( "Current top-k list is:\n" +
				// this.toResultString() );
				Long cnt = topK.get(example) + 1;
				topK.put(example, cnt);

			} else {

				// we must not monitor more than k elements
				//
				if (topK.size() >= k) {

					log.debug(
							"Need to replace the most in-frequent top-k element with {}",
							example);
					// LogService.getGlobal().logNote("Current top-k list is:\n"
					// +
					// this.toResultString() );
					//
					// find the one with the smallest count and replace it
					//
					Long min = 0L;
					Serializable leastElement = null;

					for (Serializable key : topK.keySet()) {
						if (leastElement == null) {
							min = topK.get(key);
							leastElement = key;
						} else {
							if (topK.get(key) < min) {
								min = topK.get(key);
								leastElement = key;
							}
						}
					}

					Long newCount = min + 1;
					topK.remove(leastElement);
					topK.put(example, newCount);
				} else {
					//
					// ok, there is space left in our monitor-list
					//

					log.debug("Enough space to add new element {}", example);
					log.debug("   space used: {}/{}", topK.size(), k);
					if (topK.get(example) != null)
						log.warn("Overwriting existing element with count {}",
								topK.get(example));

					topK.put(example, 1L);

				}
			}
		}
	}

	public Long getCount(Serializable item) {
		if (topK.containsKey(item))
			return this.topK.get(item);
		return 0L;
	}

	/**
	 * This implements the Comparator interface which is used to sort the top-k
	 * elements in order to find out the (k+1)-th element which is to be
	 * eliminated next.
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Serializable o1, Serializable o2) {

		if (o1 == o2 || o1.equals(o2))
			return 0;

		Long i1 = topK.get(o1);
		Long i2 = topK.get(o2);

		int rc = i1.compareTo(i2);
		if (rc == 0)
			return o1.toString().compareTo(o2 + "");

		return (-1) * i1.compareTo(i2);
	}

	/**
	 * @see stream.counter.CountModel#getTotalCount()
	 */
	public long getTotalCount() {
		return cnt;
	}

	/**
	 * @see stream.counter.CountModel#keySet()
	 */
	public Set<Serializable> keySet() {
		return topK.keySet();
	}

	public Map<Serializable, Long> getCounts() {
		synchronized (topK) {
			return new LinkedHashMap<Serializable, Long>(this.topK);
		}
	}

	/**
	 * @see stream.model.PredictionModel#predict(java.lang.Object)
	 */
	public Long predict(String item) {
		return getCount(item);
	}

	public void dumpSize() {
		log.info("Simple TopK uses {} bytes", SizeOf.sizeOf(this));
	}
}