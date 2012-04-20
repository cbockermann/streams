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
package stream.io;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import stream.data.Data;
import stream.data.DataFactory;

/**
 * 
 * @author chris
 * @deprecated
 */
public class DataStreamLoop extends DataStreamProcessor {

	List<Data> buffer = new ArrayList<Data>();

	int ptr = 0;
	boolean inLoop = false;
	Integer bufferSize = 10000;
	Boolean shuffle = true;
	Integer repeat = -1;

	public Integer getBufferSize() {
		return bufferSize;
	}

	public void setBufferSize(Integer bufferSize) {
		this.bufferSize = bufferSize;
	}

	public Boolean getShuffle() {
		return shuffle;
	}

	public void setShuffle(Boolean shuffle) {
		this.shuffle = shuffle;
	}

	public Integer getRepeat() {
		return repeat;
	}

	public void setRepeat(Integer repeat) {
		this.repeat = repeat;
	}

	@Override
	public Data readNext() throws Exception {
		return readNext(DataFactory.create());
	}

	@Override
	public Data readNext(Data data) throws Exception {

		if (inLoop) {
			if (ptr >= buffer.size()) {

				if (repeat == 1)
					return null;

				if (shuffle) {
					Collections.shuffle(buffer);
				}
				repeat--;
				ptr = 0;
			}

			return buffer.get(ptr++);
		}

		Data item = source.readNext(data);
		if (item == null) {
			inLoop = true;
			if (shuffle)
				Collections.shuffle(buffer);

			return readNext(item);
		}
		buffer.add(item);
		return item;
	}
}