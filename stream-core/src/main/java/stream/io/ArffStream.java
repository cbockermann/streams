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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.annotations.Description;
import stream.data.DataFactory;

/**
 * <p>
 * This class implements a streaming source providing information from an ARFF
 * file form.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 * 
 */
@Description(group = "Data Stream.Sources")
public class ArffStream extends AbstractDataStream {
	static Logger log = LoggerFactory.getLogger(ArffStream.class);

	/**
	 * @param url
	 * @throws Exception
	 */
	public ArffStream(SourceURL url) throws Exception {
		super(url);
	}

	public void readHeader() throws Exception {
		String line = reader.readLine();

		while (line != null && !line.startsWith("@data")) {
			if (line.startsWith("@attribute")) {
				String[] tok = line.split("\\s");
				Class<?> clazz = Object.class;
				if ("numeric".equalsIgnoreCase(tok[2].trim()))
					clazz = Double.class;

				String app = "";
				int i = 0;
				while (attributes.containsKey(tok[1] + app))
					app = "_" + (i++);

				attributes.put(tok[1] + app, clazz);
			}
			line = reader.readLine();
		}

		log.info("Attributes of Arff-Stream: {}", attributes);
	}

	/**
	 * @see stream.io.AbstractDataStream#readNext(stream.Data)
	 */
	@Override
	public Data readItem(Data datum) throws Exception {
		if (datum == null)
			datum = DataFactory.create();

		String line = reader.readLine();
		while (line != null && line.trim().isEmpty())
			line = reader.readLine();

		if (line != null && !line.trim().equals("")) {
			String[] tok = line.split(",");
			int i = 0;
			for (String name : attributes.keySet()) {
				if (i < tok.length) {
					if (Double.class.equals(attributes.get(name))) {
						datum.put(name, new Double(tok[i]));
					} else
						datum.put(name, tok[i]);
					i++;
				} else
					break;
			}
		}
		return datum;
	}

	/**
	 * @see stream.io.DataStream#close()
	 */
	@Override
	public void close() {
		try {
			reader.close();
		} catch (Exception e) {
			log.error("Failed to properly close reader: {}", e.getMessage());
			if (log.isDebugEnabled())
				e.printStackTrace();
		}
	}
}