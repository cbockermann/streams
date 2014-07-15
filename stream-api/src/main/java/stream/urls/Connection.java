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

import stream.io.SourceURL;

/**
 * <p>
 * This class provides an abstract connection to some data source. The source is
 * specified by a {@link stream.io.SourceURL} object. Implementations of this
 * class are providing access to those data sources by means of
 * protocol-specific handling.
 * </p>
 * 
 * @author Christian Bockermann
 * 
 */
public abstract class Connection {

	/** The URL to which this instance should connect to */
	final SourceURL url;

	public Connection(SourceURL url) {
		this.url = url;
	}

	/**
	 * A list of supported protocol for the implementing class. Usually a class
	 * implements supports for a single protocol.
	 * 
	 * @return
	 */
	public abstract String[] getSupportedProtocols();

	/**
	 * This opens the connection and returns the input stream for the
	 * connection.
	 * 
	 * @return
	 * @throws IOException
	 */
	public abstract InputStream connect() throws IOException;

	/**
	 * This disconnects from the source (if previously connected) and releases
	 * all resources aquired.
	 * 
	 * @throws IOException
	 */
	public abstract void disconnect() throws IOException;
}
