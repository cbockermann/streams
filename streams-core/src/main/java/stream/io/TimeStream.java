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
package stream.io;

import stream.Data;
import stream.annotations.Parameter;
import stream.data.DataFactory;
import stream.util.parser.TimeParser;

/**
 * @author chris
 * 
 */
public class TimeStream extends AbstractStream {

	String key = "@timestamp";
	String interval = "";
	Long gap = -1L;
	long last = 0L;

	/**
	 * @param url
	 */
	public TimeStream() {
		super((SourceURL) null);
	}

	/**
	 * @return the interval
	 */
	public String getInterval() {
		return interval;
	}

	/**
	 * @param interval
	 *            the interval to set
	 */
	@Parameter(description = "The time gap/rate at which this stream should provide items.", required = true)
	public void setInterval(String interval) {
		this.interval = interval;
		try {
			gap = TimeParser.parseTime(interval);
		} catch (Exception e) {
			gap = -1L;
			throw new RuntimeException("Invalid time string '" + interval
					+ "': " + e.getMessage());
		}
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
	@Parameter(description = "The name of the attribute that should hold the timestamp, defaults to `@timestamp`", required = false)
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @see stream.io.AbstractStream#readItem(stream.Data)
	 */
	@Override
	public Data readNext() throws Exception {

		Data instance = DataFactory.create();

		Long timestamp = System.currentTimeMillis();

		if (gap > 0) {

			long t = timestamp - (timestamp % gap);
			long t2 = gap - (timestamp - t);
			try {
				if (t2 > 0)
					Thread.sleep(t2);
			} catch (Exception e) {

			}
			timestamp = System.currentTimeMillis();
		}
		last = timestamp;

		instance.put(key, timestamp);
		return instance;
	}
}
