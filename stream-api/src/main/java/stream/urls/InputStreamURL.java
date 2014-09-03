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
package stream.urls;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import stream.io.SourceURL;

/**
 * @author chris
 * 
 */
public class InputStreamURL extends SourceURL {

	/** The unique class ID */
	private static final long serialVersionUID = -8598798178387924422L;
	final InputStream stream;

	public InputStreamURL(InputStream is) {
		super();
		stream = is;
	}

	/**
	 * @see stream.io.SourceURL#openStream()
	 */
	@Override
	public InputStream openStream() throws IOException {
		return stream;
	}

	/**
	 * @see stream.io.SourceURL#getFile()
	 */
	@Override
	public String getFile() {
		return "java.io.InputStream";
	}

	/**
	 * @see stream.io.SourceURL#getProtocol()
	 */
	@Override
	public String getProtocol() {
		return "direct";
	}

	/**
	 * @see stream.io.SourceURL#getHost()
	 */
	@Override
	public String getHost() {
		return "127.0.0.1";
	}

	/**
	 * @see stream.io.SourceURL#getPort()
	 */
	@Override
	public int getPort() {
		return 0;
	}

	/**
	 * @see stream.io.SourceURL#getPath()
	 */
	@Override
	public String getPath() {
		return "" + this.stream;
	}

	/**
	 * @see stream.io.SourceURL#getParameters()
	 */
	@Override
	public Map<String, String> getParameters() {
		return super.getParameters();
	}
}