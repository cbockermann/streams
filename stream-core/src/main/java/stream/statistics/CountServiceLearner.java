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
package stream.statistics;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import stream.AbstractProcessor;
import stream.Data;

/**
 * @author chris
 * 
 */
public class CountServiceLearner extends AbstractProcessor implements
		CountService {

	Long count = 0L;
	Map<String, AtomicLong> counts = new HashMap<String, AtomicLong>();

	/**
	 * @see stream.Processor#process(stream.Data)
	 */
	@Override
	public Data process(Data input) {
		count++;
		return input;
	}

	/**
	 * @see stream.statistics.CountService#getCount()
	 */
	@Override
	public Long getCount() {
		return count;
	}

	/**
	 * @see stream.service.Service#reset()
	 */
	@Override
	public void reset() throws Exception {
		count = 0L;
	}
}
