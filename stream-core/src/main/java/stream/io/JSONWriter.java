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

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

import net.minidev.json.JSONObject;
import stream.Data;
import stream.data.DataFactory;
import stream.util.KeyFilter;

/**
 * <p>
 * This is a simple JSON writer that will write all data items into JSON strings
 * (one line for each item).
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 * 
 */
public class JSONWriter extends CsvWriter {

	public JSONWriter() {
		super();
	}

	public JSONWriter(File file) throws IOException {
		super(file);
	}

	public JSONWriter(URL url) throws Exception {
		super(url);
	}

	public JSONWriter(OutputStream out) throws Exception {
		super(out);
	}

	/**
	 * @see stream.io.CsvWriter#writeHeader(stream.Data)
	 */
	@Override
	public void writeHeader(Data datum) {
		//
		// we overwrite this method to ensure no data-header is
		// written by the super-class
		//
	}

	/**
	 * @see stream.io.CsvWriter#write(stream.Data)
	 */
	@Override
	public void write(Data datum) {

		if (this.keys != null) {
			Data item = DataFactory.create();
			for (String key : KeyFilter.select(datum, keys)) {
				if (datum.containsKey(key))
					item.put(key, datum.get(key));
			}
			p.println(JSONObject.toJSONString(item));
		} else
			p.println(JSONObject.toJSONString(datum));
	}
}
