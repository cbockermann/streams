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
package stream.io;

import stream.data.Data;
import stream.util.parser.TimeParser;

/**
 * @author chris
 * 
 */
public class TimeStream extends AbstractDataStream {

	String key = "@timestamp";
	String interval = "";
	Long gap = -1L;
	long last = 0L;

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
	 * @see stream.io.DataStream#close()
	 */
	@Override
	public void close() {
	}

	/**
	 * @see stream.io.AbstractDataStream#readHeader()
	 */
	@Override
	public void readHeader() throws Exception {
	}

	/**
	 * @see stream.io.AbstractDataStream#readItem(stream.data.Data)
	 */
	@Override
	public Data readItem(Data instance) throws Exception {
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
