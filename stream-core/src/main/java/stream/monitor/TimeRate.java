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

import stream.Data;
import stream.ProcessContext;

/**
 * @author Hendrik Blom
 * 
 */
public class TimeRate extends StreamMonitor implements TimeRateService {

	static Logger logger = LoggerFactory.getLogger(TimeRate.class);
	protected Long start;
	protected Long startIndex;
	protected Long nowIndex;
	protected long n;
	protected float mean;

	protected Float rate;
	protected Float time;

	protected Integer every = null;
	protected String index;

	public TimeRate() {
		try {
			reset();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
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

	@Override
	public void init(ProcessContext ctx) throws Exception {
		if (dweet)
			// keys = new String[] { "index", "@timeRate", "@processedTime" };
			keys = new String[] { "@timeRate" };
		super.init(ctx);

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
				rate = (1f * indexDiff) / diff;
				time = (1f * indexDiff) / 1000f;

				data.put("@timeRate", rate);
				data.put("@processedTime", time);

				if (log)
					logger.info(
							"Time rate {}. {} time (s) processed. @index={}.Time-rate is: {}/second",
							getId(), time, nowIndex, rate);
				if (dweet) {
					n++;
					float delta = rate - mean;
					mean = mean + (delta / n);
					data.put("@timeRate", mean);
					dweetWriter.process(data);
				}

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

		logger.info("TimeRate finished");
	}

	@Override
	public Double getTimeRate() {
		return new Double(rate);
	}

	@Override
	public void reset() throws Exception {
		n = 0l;
		start = null;
		startIndex = null;
		nowIndex = null;
		rate = new Float(0f);
		time = new Float(0f);
		mean = 0;

	}
}