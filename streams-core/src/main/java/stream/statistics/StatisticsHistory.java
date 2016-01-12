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
package stream.statistics;

import java.util.LinkedHashMap;

import stream.data.Statistics;

/**
 * @author chris
 * 
 */
public class StatisticsHistory extends History<Statistics> {

	/** The unique class ID */
	private static final long serialVersionUID = -2712326723596068372L;

	/**
	 * @param stepSize
	 * @param historyLength
	 */
	public StatisticsHistory(long stepSize, long historyLength) {
		super(stepSize, historyLength);
	}

	public StatisticsHistory(StatisticsHistory sh) {
		super(sh.stepSize, sh.historyLength);
		this.map = new LinkedHashMap<Long, Statistics>(sh.map);
	}

	/**
	 * @see stream.statistics.History#add(java.lang.Long, java.lang.Object)
	 */
	@Override
	public void add(Long timestamp, Statistics data) {

		Long x = adjust(timestamp);
		Statistics st = get(x);
		if (st == null) {
			st = new Statistics(data);
			this.map.put(x, st);
		} else {
			st.add(data);
		}
		this.last = x;
	}
}
