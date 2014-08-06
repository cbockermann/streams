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
package stream.monitor;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.AbstractProcessor;
import stream.Data;
import stream.ProcessContext;
import stream.data.Statistics;
import stream.statistics.StatisticsService;

/**
 * @author Hendrik Blom
 * 
 */
public class TimeRate extends AbstractProcessor implements StatisticsService {

	static Logger log = LoggerFactory.getLogger(TimeRate.class);
	protected Long start = null;
	protected Long startIndex = null;
	protected Long nowIndex = null;

	protected Double rate = new Double(0.0);

	protected Integer every = null;
	protected String id;
	protected String index;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	/**
	 * @see stream.AbstractProcessor#init(stream.ProcessContext)
	 */
	@Override
	public void init(ProcessContext ctx) throws Exception {
		super.init(ctx);
		// start = System.currentTimeMillis();
	}

	@Override
	public Data process(Data data) {
		if (start == null) {
			start = System.currentTimeMillis();
			startIndex = getIndex(data);
		}
		Long now = System.currentTimeMillis();
		long diff = now - start;
		if (diff > every) {

			nowIndex = getIndex(data);
			if (nowIndex != null) {
				long indexDiff = nowIndex - startIndex;
				rate = (1d * indexDiff) / diff;
				log.info("Data rate '" + getId()
						+ "': {} time (s) processed, time-rate is: {}/second",
						indexDiff / 1000, rate);
				start = now;
				startIndex = nowIndex;
			}
		}
		return data;
	}

	private Long getIndex(Data data) {
		Serializable s = data.get(index);
		if (s != null && s instanceof Long)
			return (Long) s;
		return null;
	}

	/**
	 * @see stream.AbstractProcessor#finish()
	 */
	@Override
	public void finish() throws Exception {
		super.finish();

		log.info("TimeRate finished");
	}

	@Override
	public void reset() throws Exception {
	}

	@Override
	public Statistics getStatistics() {
		Statistics st = new Statistics();
		synchronized (rate) {
			st.put("dataRate", new Double(rate.doubleValue()));
		}
		return st;
	}

	/**
	 * @return the every
	 */
	public Integer getEvery() {
		return every;
	}

	/**
	 * @param every
	 *            the every to set
	 */
	public void setEvery(Integer every) {
		this.every = every;
	}

}