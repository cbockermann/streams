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
package stream.learner;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.AbstractProcessor;
import stream.annotations.Description;
import stream.annotations.Parameter;
import stream.data.Data;
import stream.data.Statistics;
import stream.mining.counter.TopKCounter;
import stream.statistics.History;
import stream.statistics.StatisticsHistory;

/**
 * @author chris
 * 
 */
@Description(group = "Data Stream.Mining")
public class MetaDataLearner extends AbstractProcessor implements
		MetaDataService {

	static Logger log = LoggerFactory.getLogger(MetaDataLearner.class);

	String[] keys = null;

	final Set<String> observed = new LinkedHashSet<String>();
	final Map<String, Class<?>> schema = new LinkedHashMap<String, Class<?>>();
	final Map<String, Statistics> stats = new LinkedHashMap<String, Statistics>();
	final Map<String, TopKCounter> counter = new LinkedHashMap<String, TopKCounter>();

	final Map<String, Long> missingValues = new LinkedHashMap<String, Long>();

	final StreamInfo streamInfo = new StreamInfo();

	StatisticsHistory history = new StatisticsHistory(History.SECOND,
			History.MINUTE);

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
	@Parameter(required = false, description = "The keys/attributes to observe")
	public void setKeys(String[] keys) {
		this.keys = keys;
		if (this.keys != null && this.keys.length == 0)
			this.keys = null;

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

		synchronized (streamInfo) {
			if (streamInfo.firstItem == null)
				streamInfo.firstItem = new Date();

			streamInfo.lastItem = new Date();
			streamInfo.numberOfItems++;
		}

		synchronized (history) {
			Statistics data = new Statistics();
			data.add("items", 1.0d);
			history.add(System.currentTimeMillis(), data);
		}

		if (keys == null) {
			observed.addAll(input.keySet());
		} else {
		}

		for (String key : observed) {

			Serializable val = input.get(key);
			if (val != null) {

				if (!schema.containsKey(key)) {
					schema.put(key, val.getClass());
				}

				Statistics st = stats.get(key);
				if (st == null) {
					log.debug(
							"Creating new statistics object for attribute {}",
							key);
					st = new Statistics(key);
					stats.put(key, st);
				}

				if (val instanceof Number) {
					update(st, ((Number) val).doubleValue());
				} else {

					TopKCounter count = counter.get(key);
					if (count == null) {
						count = new TopKCounter(100);
						counter.put(key, count);
					}
					count.learn(val.toString());
				}
			} else {
				Long miss = missingValues.get(key);
				if (miss == null) {
					miss = 1L;
				} else {
					miss += 1L;
				}
				missingValues.put(key, miss);
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

	/**
	 * @see stream.learner.MetaDataService#getStatistics(java.lang.String)
	 */
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
	public Map<Serializable, Long> getTopElements(String key) {
		TopKCounter count = counter.get(key);
		Map<Serializable, Long> map = new LinkedHashMap<Serializable, Long>();
		for (Serializable k : count.keySet()) {
			map.put(k, count.getCount(k));
		}
		return map;
	}

	/**
	 * @see stream.service.Service#reset()
	 */
	@Override
	public void reset() throws Exception {
		this.counter.clear();
		this.observed.clear();
		this.streamInfo.reset();
		this.history.clear();
		this.missingValues.clear();
		this.schema.clear();
	}

	/**
	 * @see stream.learner.MetaDataService#getMetaData()
	 */
	@Override
	public Map<String, Class<?>> getMetaData() {
		synchronized (schema) {
			return new LinkedHashMap<String, Class<?>>(schema);
		}
	}

	/**
	 * @see stream.learner.MetaDataService#getMetaDataStatistics()
	 */
	@Override
	public Map<String, Statistics> getMetaDataStatistics() {
		synchronized (stats) {
			return new LinkedHashMap<String, Statistics>(this.stats);
		}
	}

	/**
	 * @see stream.learner.MetaDataService#getStreamInformation()
	 */
	@Override
	public StreamInfo getStreamInformation() {

		StreamInfo info = new StreamInfo();
		synchronized (streamInfo) {
			if (streamInfo.firstItem != null)
				info.firstItem = new Date(streamInfo.firstItem.getTime());
			if (streamInfo.lastItem != null)
				info.lastItem = new Date(streamInfo.lastItem.getTime());
			info.numberOfItems = new Long(streamInfo.numberOfItems);
		}
		return info;
	}

	/**
	 * @see stream.learner.MetaDataService#getStreamHistory()
	 */
	@Override
	public StatisticsHistory getStreamHistory() {
		//
		// TODO: This is not yet thread-safe!
		//
		try {
			synchronized (history) {
				log.info("History has {} items", history.getData().size());
				return new StatisticsHistory(history);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}