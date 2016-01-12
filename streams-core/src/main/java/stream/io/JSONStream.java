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
import java.io.Serializable;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.annotations.Description;
import stream.data.DataFactory;

/**
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 * 
 */
@Description(group = "Data Stream.Sources")
public class JSONStream extends AbstractLineStream {

	private final Logger log = LoggerFactory.getLogger(JSONStream.class);
	private final JSONParser parser = new JSONParser(
			JSONParser.DEFAULT_PERMISSIVE_MODE);

	/**
	 * @param in
	 * @throws Exception
	 */
	public JSONStream(InputStream in) throws Exception {
		super(in);
	}

	public JSONStream(SourceURL url) throws Exception {
		super(url);
	}

	public JSONStream() throws Exception {
		super();
	}

	@Override
	public void init() throws Exception {
		super.init();
	}

	/**
	 * @see stream.io.AbstractStream#readItem(stream.Data)
	 */
	@Override
	public Data readNext() throws Exception {

		Data instance = DataFactory.create();

		String line = readLine();
		if (line == null) {
			return null;
		}

		while (line != null) {
			try {
				log.debug("line: {}", line);

				log.debug("Parsing item from {}", line);
				JSONObject object = parser.parse(line, JSONObject.class);
				if (object != null) {
					for (String key : object.keySet()) {
						Object val = object.get(key);
						if (val == null)
							continue;
						if (val instanceof Serializable)
							instance.put(key, (Serializable) val);
						else
							instance.put(key, val.toString());
					}
				} else {
					log.debug("Failed to parse item, object = {}", object);
				}

				log.debug("returning instance: {}", instance);
				return instance;
			} catch (Exception e) {
				log.error("Failed to parse line: {}", line);
				e.printStackTrace();
				line = readLine();
			}
		}

		return null;
	}
}