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
package stream.util;

import java.io.Serializable;

import stream.util.parser.TimeFormat;
import stream.util.parser.TimeParser;

/**
 * <p>
 * This is a very simple class that represents time.
 * </p>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public class Time implements Serializable {

	/** The unique class ID */
	private static final long serialVersionUID = 5508820209522304182L;

	public final static long SECOND = 1000L;
	public final static long MINUTE = 60 * SECOND;
	public final static long HOUR = 60 * MINUTE;
	public final static long DAY = 24 * HOUR;
	public final static long WEEK = 7 * DAY;
	public final static long MONTH = 30 * DAY + DAY / 2;
	public final static long YEAR = 12 * MONTH;

	final Long millis;

	public Time(String time) throws Exception {
		millis = TimeParser.parseTime(time);
	}

	public Time(Long millis) {
		this.millis = millis;
	}

	public Long asMillis() {
		return millis;
	}

	public Long asSeconds() {
		return millis / 1000L;
	}

	public Long asMinutes() {
		return millis / (60 * 1000L);
	}

	public String toString() {
		return new TimeFormat().format(millis);
	}
}
