/*
 *  streams library
 *
 *  Copyright (C) 2011-2012 by Christian Bockermann, Hendrik Blom
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

import java.net.URL;
import java.net.URLConnection;

import stream.AbstractProcessor;
import stream.data.Data;

/**
 * @author chris
 * 
 */
public class CsvUpload extends AbstractProcessor {

	URL url;

	public CsvUpload(URL url) {
		this.url = url;
	}

	/**
	 * @see stream.io.CsvWriter#dataArrived(stream.data.Data)
	 */
	@Override
	public Data process(Data datum) {

		try {
			URLConnection con = url.openConnection();
			con.setDoInput(false);
			con.setDoOutput(true);

			CsvWriter writer = new CsvWriter(
					con.getOutputStream());

			Data item = writer.process(datum);
			writer.finish();
			return item;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return datum;
	}
}
