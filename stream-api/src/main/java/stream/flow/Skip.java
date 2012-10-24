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
package stream.flow;

import stream.ConditionedProcessor;
import stream.Data;
import stream.annotations.Description;

/**
 * <p>
 * This class provides a data process that will skip (i.e. return
 * <code>null</code>) all data items matching a given condition.
 * </p>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
@Description(group = "Data Stream.Flow")
public class Skip extends ConditionedProcessor {

	/**
	 * @see stream.DataProcessor#process(stream.Data)
	 */
	@Override
	public Data process(Data data) {
		if (this.matches(data))
			return null;

		return data;
	}

	/**
	 * @see stream.ConditionedProcessor#processMatchingData(stream.Data)
	 */
	@Override
	public Data processMatchingData(Data data) {
		return data;
	}
}