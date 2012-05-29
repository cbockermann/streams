/**
 * 
 */
package stream.distribution;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.counter.Counter;
import stream.counter.ExactCounter;

/**
 * <p>
 * This class implements a histogram-model observed from a data stream. The
 * input data elements are regarded to be nominal elements, i.e. Java Strings.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 * 
 */
public class NominalDistribution<T extends Serializable> implements
		Distribution<T> {

	/** The unique class ID */
	private static final long serialVersionUID = -4642672370564928117L;

	/* A global logger for this class */
	static Logger log = LoggerFactory.getLogger(NominalDistribution.class);

	final Counter<T> counter;

	/**
	 * Creates a new nominal distribution model with an infinite number of
	 * objects being tracked. Infinite here depends on available memory and is
	 * initially assumed as <code>Integer.MAX_VALUE</code>
	 */
	public NominalDistribution() {
		counter = new ExactCounter<T>();
	}

	public NominalDistribution(Counter<T> counter) {
		this.counter = counter;
	}

	/**
	 * Add a new value to the model.
	 * 
	 * @param newVal
	 */
	public void update(T newVal) {
		counter.count(newVal);
	}

	/**
	 * @see stream.model.DistributionModel#getHistogram()
	 */
	public Map<T, Double> getHistogram() {
		Map<T, Double> map = new LinkedHashMap<T, Double>();
		for (T key : counter.keySet())
			map.put(key, counter.getCount(key).doubleValue());
		return map;
	}

	public Long getCount(T value) {
		return counter.getCount(value);
	}

	public Set<T> getElements() {
		return Collections.unmodifiableSet(counter.keySet());
	}

	public Long getTotalCount() {
		return this.counter.getTotalCount();
	}

	/**
	 * @see stream.model.Distribution#prob(java.io.Serializable)
	 */
	@Override
	public Double prob(T value) {
		Long cnt = counter.getCount(value);
		return cnt.doubleValue() / counter.getTotalCount().doubleValue();
	}
}