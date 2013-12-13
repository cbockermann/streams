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
package stream.io.multi;

import java.util.Map;

import stream.Data;
import stream.data.DataFactory;
import stream.io.Stream;

/**
 * *
 * <p>
 * A simple multi stream implementation, that merges the items of the
 * substreams.
 * </p>
 * 
 * @author Hendrik Blom
 * 
 */
public class MergeAllMultiStream extends AbstractMultiStream {


	/*
	 * @see stream.io.AbstractStream#readNext()
	 */
	@Override
	public Data readNext() throws Exception {
		Data item = DataFactory.create();
		boolean stop = true;

		for (String id : additionOrder) {
			Stream s = streams.get(id);
			Data d = s.read();
			if (d != null) {
				item.putAll(d);
				stop = false;
			}
		}
		if (stop)
			return null;
		return item;
	}

}