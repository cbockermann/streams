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
package streams.performance;

import java.text.DecimalFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.AbstractProcessor;
import stream.Data;
import stream.ProcessContext;
import stream.data.Statistics;
import streams.logging.Rlog;

public class DataRate extends AbstractProcessor {

	final DecimalFormat fmt = new DecimalFormat("0.000");
	static Logger log = LoggerFactory.getLogger(DataRate.class);

	Rlog rlog = null;
	String clock = null;
	Long count = 0L;
	Long start = 0L;

	Long windowCount = 0L;
	Long last = 0L;
	Double elapsed = 0.0d;
	Double rate = 0.0;

	Integer every = null;
	String key = "dataRate";
	String id;

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

	public String getClock() {
		return clock;
	}

	public void setClock(String clock) {
		this.clock = clock;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key
	 *            the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @see stream.AbstractProcessor#init(stream.ProcessContext)
	 */
	@Override
	public void init(ProcessContext ctx) throws Exception {
		super.init(ctx);
		// start = System.currentTimeMillis();
		rlog = new Rlog();
	}

	@Override
	public Data process(Data input) {

		if (start == 0L)
			start = System.currentTimeMillis();

		count++;

		if (every != null && count % every == 0) {
			printDataRate(System.currentTimeMillis());
		}

		Long t = System.currentTimeMillis() - start;
		if (t > 0 && count % 10 == 0) {
			synchronized (rate) {
				rate = this.count.doubleValue() / (t.doubleValue() / 1000.0d);
			}
		}

		return input;
	}

	public void printDataRate() {
		printDataRate(System.currentTimeMillis());
	}

	protected void printDataRate(Long now) {
		Double sec = (now - start) / 1000.0;
		if (sec > 0) {
            double rate = count.doubleValue() / sec;
			log.info("Data rate '" + getId() + "': {} items processed, data-rate is: {}/second",
                    count, fmt.format(rate));

			rlog.message().add("items-per-second", rate).send();
		}
	}

	/**
	 * @see stream.AbstractProcessor#finish()
	 */
	@Override
	public void finish() throws Exception {
		super.finish();

		if (start != null) {
			Long now = System.currentTimeMillis();
			Long sec = (now - start);
			log.info("DataRate processor '" + id + "' has been running for {} ms, {} items.",
                    sec, count.doubleValue());
			Double s = sec.doubleValue() / 1000.0d;
			if (s > 0)
				log.info("Overall average data-rate for processor '{}' is: {}/second", id,
						fmt.format(count.doubleValue() / s));
		} else {
			log.info("Start time not available.");
		}
	}

	public void reset() throws Exception {
		count = 0L;
		windowCount = 1L;
		last = 0L;
		start = null;
	}

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