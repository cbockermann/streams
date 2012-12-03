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
package stream.runtime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Context;
import stream.Data;
import stream.data.DataFactory;
import stream.io.AbstractStream;
import stream.io.SourceURL;
import stream.util.parser.TimeParser;

/**
 * @author chris
 * 
 */
public class Monitor extends DefaultProcess {

	static Logger log = LoggerFactory.getLogger(Monitor.class);
	Long interval = 1000L;
	String intervalString = "1000ms";

	public Monitor() {
	}

	/**
	 * @return the interval
	 */
	public String getInterval() {
		return intervalString;
	}

	/**
	 * @param interval
	 *            the interval to set
	 */
	public void setInterval(String intervalString) {
		this.intervalString = intervalString;
	}

	/**
	 * @see stream.runtime.AbstractProcess#init(stream.ProcessContext)
	 */
	@Override
	public void init(Context context) throws Exception {
		super.init(context);

		try {
			interval = TimeParser.parseTime(getInterval());
			log.debug("Monitor-interval is {} ms", interval);

			source = new AbstractStream((SourceURL) null) {
				@Override
				public Data readNext() throws Exception {
					return DataFactory.create();
				}
			};

		} catch (Exception e) {
			interval = 1000L;
			throw new Exception("Failed to initialize Monitor: "
					+ e.getMessage());
		}
	}

	public Data process(Data item) {
		Data data = super.process(item);
		try {
			Thread.sleep(interval);
		} catch (InterruptedException e) {
			log.debug("Monitor finished.");
			return data;
		}
		return data;
	}

	public String toString() {
		return "Monitor[" + super.toString() + "]";
	}
}