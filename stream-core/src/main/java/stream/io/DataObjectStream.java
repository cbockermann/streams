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
package stream.io;

import java.io.InputStream;
import java.io.ObjectInputStream;

import stream.Data;

/**
 * @author chris
 * 
 */
public class DataObjectStream extends AbstractStream {

	protected ObjectInputStream input;

	public DataObjectStream(SourceURL url) {
		super(url);
	}

	public DataObjectStream(InputStream in) {
		super(in);
	}

	/**
	 * @see stream.io.Stream#close()
	 */
	@Override
	public void close() throws Exception {
		input.close();
	}

	/**
	 * @see stream.io.Stream#init()
	 */
	@Override
	public void init() throws Exception {
		input = new ObjectInputStream(getInputStream());
	}

	/**
	 * @see stream.io.Stream#read()
	 */
	@Override
	public Data readNext() throws Exception {
		return (Data) input.readObject();
	}
}
