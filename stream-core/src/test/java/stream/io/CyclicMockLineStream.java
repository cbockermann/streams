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
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.data.DataFactory;

/**
 * @author chris
 * 
 */
public class CyclicMockLineStream extends AbstractStream {

	static Logger log = LoggerFactory.getLogger(CyclicMockLineStream.class);
	int count = 0;

	ArrayList<String> lines;
	Integer lineCount = 100;

	public CyclicMockLineStream(URL source, int lineCount) throws IOException {
		super(new SourceURL(source));
		this.lineCount = lineCount;
	}

	/**
	 * @see stream.io.AbstractStream#init()
	 */
	@Override
	public void init() throws Exception {
		super.init();

		lines = new ArrayList<String>(lineCount);
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				this.getInputStream()));
		String line = reader.readLine();
		int i = 0;
		while (line != null && i++ < lineCount) {
			lines.add(line);
			line = reader.readLine();
		}
	}

	/**
	 * @see stream.io.AbstractStream#readNext()
	 */
	@Override
	public Data readNext() throws Exception {
		Data item = DataFactory.create();
		item.put("LINE", lines.get(count++ % lines.size()));
		return item;
	}
}