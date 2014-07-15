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

import stream.Data;
import stream.ProcessorList;

/**
 * @author chris
 * 
 */
public class Wait extends ProcessorList {

	protected long n;
	long count;

	public Wait() {
		n = 1000l;
		count = 0l;
	}

	/**
	 * @see stream.ProcessorList#process(stream.Data)
	 */
	@Override
	public Data process(Data input) {
		count++;
		if (count > n) {
			count = 0l;
			return super.process(input);
		}
		return input;
	}

	/**
	 * @return the n
	 */
	public Long getN() {
		return n;
	}

	/**
	 * @param n
	 *            the n to set
	 */
	public void setN(Long n) {
		this.n = n;
	}
}
