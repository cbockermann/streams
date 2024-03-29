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
package stream.runtime;

import stream.Data;
import stream.io.Stream;

/**
 * @author chris
 * 
 */
public class StreamLifeCycle implements LifeCycle, Stream {

	final Stream stream;

	public StreamLifeCycle(Stream stream) {
		this.stream = stream;
	}

	/**
	 * @see stream.runtime.LifeCycle#init(stream.Context)
	 */
	@Override
	public void init(ApplicationContext context) throws Exception {
	}

	/**
	 * @see stream.runtime.LifeCycle#finish()
	 */
	@Override
	public void finish() throws Exception {
		stream.close();
	}

	/**
	 * @return
	 * @see stream.io.Source#getId()
	 */
	public String getId() {
		return stream.getId();
	}

	/**
	 * @param id
	 * @see stream.io.Source#setId(java.lang.String)
	 */
	public void setId(String id) {
		stream.setId(id);
	}

	/**
	 * @throws Exception
	 * @see stream.io.Source#init()
	 */
	public void init() throws Exception {
		stream.init();
	}

	/**
	 * @return
	 * @throws Exception
	 * @see stream.io.Source#read()
	 */
	public Data read() throws Exception {
		return stream.read();
	}

	/**
	 * @throws Exception
	 * @see stream.io.Source#close()
	 */
	public void close() throws Exception {
		stream.close();
	}

	/**
	 * @return
	 * @see stream.io.Stream#getLimit()
	 */
	public Long getLimit() {
		return stream.getLimit();
	}

	/**
	 * @param limit
	 * @see stream.io.Stream#setLimit(java.lang.Long)
	 */
	public void setLimit(Long limit) {
		stream.setLimit(limit);
	}

}
