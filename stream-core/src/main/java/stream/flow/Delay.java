/*
 *  stream.ai
 *
 *  Copyright (C) 2011-2012 by Christian Bockermann, Hendrik Blom
 * 
 *  stream.ai is a library, API and runtime environment for processing high
 *  volume data streams. It is composed of three submodules "stream-api",
 *  "stream-core" and "stream-runtime".
 *
 *  The stream.ai library (and its submodules) is free software: you can 
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
package stream.flow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.ConditionedProcessor;
import stream.ProcessContext;
import stream.annotations.Description;
import stream.annotations.Parameter;
import stream.data.Data;
import stream.util.parser.TimeParser;

/**
 * A simple processor that artificially delays the data processing by a
 * specified amount of time. Useful for watching high-speed data streams being
 * processed with visual components attached.
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
@Description(group = "Data Stream.Flow")
public class Delay extends ConditionedProcessor {

	static Logger log = LoggerFactory.getLogger(Delay.class);

	Long milliseconds = 0L;
	String time = "1000ms";

	/**
	 * @return the time
	 */
	public String getTime() {
		return time;
	}

	/**
	 * @param time
	 *            the time to set
	 */
	@Parameter(required = true, defaultValue = "1000ms")
	public void setTime(String time) {
		this.time = time;
	}

	/**
	 * @see stream.AbstractProcessor#init()
	 */
	@Override
	public void init(ProcessContext ctx) throws Exception {
		super.init(ctx);
		if (time != null && !"".equals(time.trim()))
			milliseconds = TimeParser.parseTime(time);
		else
			milliseconds = 0L;
	}

	/**
	 * @see stream.Processor#process(stream.data.Data)
	 */
	@Override
	public Data processMatchingData(Data data) {
		if (milliseconds > 0) {
			try {
				Thread.sleep(milliseconds);
			} catch (Exception e) {
				log.error("Failed to delay execution: {}", e.getMessage());
			}
		}

		return data;
	}
}