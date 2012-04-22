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
package stream.statistics;

import stream.data.Data;

/**
 * @author chris
 * 
 */
public class Average extends Sum {

	Double count = 0.0d;

	/**
	 * @see stream.statistics.Sum#updateStatistics(stream.data.Data)
	 */
	@Override
	public void updateStatistics(Data item) {
		count += 1.0d;

		for (String key : keys) {
			Double val = null;
			try {
				val = new Double(item.get(key) + "");
			} catch (Exception e) {
				val = null;
			}

			if (val != null) {
				statistics.add(key, val);
				item.put(key, statistics.get(key) / count);
			}
		}
	}
}
