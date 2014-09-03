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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author chris,hendrik
 * 
 */
public abstract class AbstractLineStream extends AbstractStream {

	protected Map<String, Class<?>> attributes;
	protected BufferedReader reader;
	protected String encoding;

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	/**
	 * @param in
	 */
	public AbstractLineStream(SourceURL url) {
		super(url);
	}

	/**
	 * @param in
	 */
	public AbstractLineStream(InputStream in) {
		super(in);
	}

	public AbstractLineStream() throws Exception {
		super();
	}

	/**
	 * @see stream.io.Source#close()
	 */
	@Override
	public void close() throws Exception {
		if (reader != null)
			reader.close();
		super.close();
	}

	/**
	 * @see stream.io.Stream#init()
	 */
	@Override
	public void init() throws Exception {
		super.init();
		attributes = new LinkedHashMap<String, Class<?>>();

		if (encoding != null)
			reader = new BufferedReader(new InputStreamReader(getInputStream(),
					encoding));
		else
			reader = new BufferedReader(new InputStreamReader(getInputStream()));
	}

	public String readLine() throws Exception {
		return reader.readLine();
	}
}
