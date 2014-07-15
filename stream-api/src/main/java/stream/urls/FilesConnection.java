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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import stream.io.SequentialFileInputStream;
import stream.io.SourceURL;

/**
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public class FilesConnection extends Connection {

	protected SequentialFileInputStream seqFileStream;

	/**
	 * @param url
	 */
	public FilesConnection(SourceURL url) {
		super(url);
	}

	/**
	 * @see stream.urls.Connection#getSupportedProtocols()
	 */
	@Override
	public String[] getSupportedProtocols() {
		return new String[] { "files" };
	}

	/**
	 * @see stream.urls.Connection#connect()
	 */
	@Override
	public InputStream connect() throws IOException {

		boolean removeAfterRead = "true".equalsIgnoreCase(url.getParameters()
				.get("remove"))
				|| "true".equalsIgnoreCase(url.getParameters().get(
						"removeAfterRead"));

		String pattern = ".*";
		if (url.getParameters().containsKey("pattern")) {
			pattern = url.getParameters().get("pattern");
		}

		File file = new File(url.getPath());
		seqFileStream = new SequentialFileInputStream(file, pattern,
				removeAfterRead);

		if (url.getParameters().containsKey("maxWaitingTime")) {
			seqFileStream.setMaxWaitingTime(new Long(url.getParameters().get(
					"maxWaitingTime")));
		}

		return seqFileStream;
	}

	/**
	 * @see stream.urls.Connection#disconnect()
	 */
	@Override
	public void disconnect() throws IOException {
		if (seqFileStream != null) {
			seqFileStream.close();
			seqFileStream = null;
		}
	}
}
