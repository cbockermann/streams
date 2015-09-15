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
package stream.flow;

import java.io.Serializable;

import stream.Data;

/**
 * a Long index;
 * 
 * @author Hendrik Blom
 * 
 */
public class LongIndex extends Index {

	protected long startIndex;
	protected long index;

	@Override
	public Data process(Data data) {
		Serializable s = data.get(indexKey);
		if (s != null && s instanceof Long) {
			long id = (Long) s;
			// Start index
			if (startIndex < 0) {
				startIndex = id;
				index = 0;
				data.put(indexId, index);
				return data;
			}
			index = id - startIndex;
			data.put(indexId, index);
			return data;

		}
		data.put(indexId, index);
		return data;

	}

	@Override
	public void reset() throws Exception {
		startIndex = -1l;
		index = 0;
	}

}
