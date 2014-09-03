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
import stream.Data;
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
	 * @see stream.Processor#process(stream.Data)
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
