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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.expressions.version2.ConditionedProcessor;

/**
 * <p>
 * This class provides a data process that will skip (i.e. return
 * <code>null</code>) all data items matching a given condition.
 * </p>
 * 
 * @author Hendrik Blom
 * 
 */
// @Description(group = "Data Stream.Flow")
public class Skip extends ConditionedProcessor {

	static Logger log = LoggerFactory.getLogger(Skip.class);

	Long count = null;
	Long seen = 0L;

	/**
	 * @see stream.DataProcessor#process(stream.Data)
	 */
	@Override
	public Data process(Data data) {
		try {

			seen++;
			if (count != null) {
				if (count < seen) {
					return null;
				} else {
					return data;
				}
			} else {
				if (this.matches(data))
					return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}

	public boolean matches(Data item) throws Exception {
		if (condition == null)
			return true;
		final Boolean b = condition.get(context, item);
		if (b == null)
			return false;
		return b;
	}

	/**
	 * @see stream.ConditionedProcessor#processMatchingData(stream.Data)
	 */
	@Override
	public Data processMatchingData(Data data) {
		return data;
	}
}