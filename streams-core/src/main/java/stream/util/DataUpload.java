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
package stream.util;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.io.CsvStream;
import stream.io.CsvUpload;
import stream.io.JSONStream;
import stream.io.SourceURL;
import stream.io.Stream;

/**
 * @author chris
 * @deprecated This class is too specific to belong to the streams-core package.
 */
public class DataUpload {

	static Logger log = LoggerFactory.getLogger(DataUpload.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		URL url = null;
		String remoteUrl = "rmi://kirmes.cs.uni-dortmund.de:9106/DataIndex";

		if (args.length > 0)
			url = new URL(args[0]);
		else
			url = new URL("http://kirmes.cs.uni-dortmund.de/data/adult.tr");

		if (args.length > 1)
			remoteUrl = args[1];

		Stream stream = null;
		String fmt = url.toString();
		if (fmt.endsWith(".csv") || fmt.endsWith(".csv.gz")) {
			stream = new CsvStream(new SourceURL(url));
		}

		if (fmt.endsWith(".json") || fmt.endsWith(".json.gz")) {
			stream = new JSONStream(new SourceURL(url));
		}

		if (stream == null) {
			System.err.println("Failed to detect data stream format...");
			System.exit(-1);
		}

		int delay = -1;
		try {
			delay = new Integer(System.getProperty("delay"));
		} catch (Exception e) {
			delay = -1;
		}

		CsvUpload upload = new CsvUpload(new URL(remoteUrl));
		int limit = 10000;
		int i = 0;
		Data item = stream.read();
		while (item != null && limit-- > 0) {

			if (i > 0 && i % 100 == 0) {
				log.info("{} items inserted.", i);
			}

			upload.process(item);

			if (delay > 0) {
				Thread.sleep(delay);
			}

			item = stream.read();
			i++;
		}
	}
}
