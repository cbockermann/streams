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
package stream.data.stats;

import stream.AbstractProcessor;
import stream.Data;

/**
 * @author chris
 * 
 */
public class CountCheck extends AbstractProcessor {

	Long myCount = 0L;

	CountService countService = null;

	/**
	 * @return the countService
	 */
	public CountService getCountService() {
		return countService;
	}

	/**
	 * @param countService
	 *            the countService to set
	 */
	public void setCountService(CountService countService) {
		this.countService = countService;
	}

	/**
	 * @see stream.Processor#process(stream.Data)
	 */
	@Override
	public Data process(Data input) {
		myCount++;

		if (countService == null)
			throw new RuntimeException("No countService has been injected!");

		Long count = countService.getCount();
		if (!myCount.equals(count)) {
			throw new RuntimeException("Count of CountService mismatches!");
		}

		return input;
	}
}
