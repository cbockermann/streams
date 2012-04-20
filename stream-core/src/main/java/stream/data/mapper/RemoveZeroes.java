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
package stream.data.mapper;

import java.util.ArrayList;
import java.util.List;

import stream.Processor;
import stream.annotations.Description;
import stream.data.Data;
import stream.data.DataUtils;

/**
 * @author chris
 * 
 */
@Description(group = "Data Stream.Processing.Transformations.Data")
public class RemoveZeroes implements Processor {

	/**
	 * @see stream.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data data) {
		List<String> remove = new ArrayList<String>();

		for (String key : data.keySet()) {

			if (DataUtils.isSpecial(key))
				continue;

			try {
				Double val = new Double(data.get(key).toString());
				if (val == 0.0d)
					remove.add(key);
			} catch (Exception e) {
			}
		}

		for (String key : remove) {
			data.remove(key);
		}

		return data;
	}
}