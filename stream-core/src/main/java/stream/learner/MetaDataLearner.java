/**
 * 
 */
package stream.learner;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.AbstractProcessor;
import stream.data.Data;
import stream.data.Statistics;

/**
 * @author chris
 * 
 */
public class MetaDataLearner extends AbstractProcessor implements
		MetaDataService {

	static Logger log = LoggerFactory.getLogger(MetaDataLearner.class);

	String[] keys = null;

	final Set<String> observed = new LinkedHashSet<String>();
	Map<String, Statistics> stats = new LinkedHashMap<String, Statistics>();

	/**
	 * @return the keys
	 */
	public String[] getKeys() {
		return keys;
	}

	/**
	 * @param keys
	 *            the keys to set
	 */
	public void setKeys(String[] keys) {
		this.keys = keys;
		if (this.keys != null) {
			for (String key : this.keys) {
				observed.add(key);
			}
		}
	}

	/**
	 * @see stream.Processor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data input) {

		if (keys == null) {
			observed.addAll(input.keySet());
		}

		for (String key : observed) {

			Serializable val = input.get(key);
			if (val != null) {

				Statistics st = stats.get(key);
				if (st == null) {
					log.debug(
							"Creating new statistics object for attribute {}",
							key);
					st = new Statistics(key);
					stats.put(key, st);
				}

				if (val instanceof Double) {
					update(st, (Double) val);
				} else {
					update(st, val.toString());
				}
			}

		}

		return input;
	}

	protected Statistics update(Statistics st, Double value) {

		Double min = st.get("minimum");
		if (min == null)
			min = value;
		else
			min = Math.min(min, value);
		st.put("minimum", min);

		Double max = st.get("maximum");
		if (max == null)
			max = value;
		else
			max = Math.max(max, value);
		st.put("maximum", max);

		st.add("sum", value);
		st.add("count", 1.0d);

		st.put("average", st.get("sum") / st.get("count"));
		return st;
	}

	protected Statistics update(Statistics st, String value) {
		st.add(value, 1.0d);
		return st;
	}

	/**
	 * @see stream.learner.MetaDataService#getStatistics(java.lang.String)
	 */
	@Override
	public Statistics getStatistics(String key) {
		if (!stats.containsKey(key)) {
			return null;
		} else {
			Statistics copy;
			Statistics st = stats.get(key);
			synchronized (st) {
				copy = new Statistics(st);
			}
			return copy;
		}
	}

	/**
	 * @see stream.learner.MetaDataService#getTopValues(java.lang.String)
	 */
	@Override
	public Set<Serializable> getTopValues(String key) {
		return new HashSet<Serializable>();
	}

	/**
	 * @see stream.service.Service#reset()
	 */
	@Override
	public void reset() throws Exception {
	}
}
