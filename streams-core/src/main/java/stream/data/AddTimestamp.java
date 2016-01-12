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
package stream.data;

import stream.Data;
import stream.Processor;
import stream.annotations.Description;
import stream.annotations.Parameter;

/**
 * @author chris
 * 
 */
@Description(group = "Streams.Transformations.Annotations")
public class AddTimestamp implements Processor {

	String key = "@timestamp";

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key
	 *            the key to set
	 */
	@Parameter(description = "The key of the timestamp attribute to add")
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @see stream.Processor#process(stream.Data)
	 */
	@Override
	public Data process(Data input) {
		if (key != null) {
			input.put(key, System.currentTimeMillis());
		}
		return input;
	}
}