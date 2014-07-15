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
package stream.lookup;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.io.JSONStream;
import stream.io.SourceURL;

/**
 * <p>
 * This implementation of the AbstractDatabase lookup service uses a JSON stream
 * to populate the lookup table.
 * </p>
 * 
 * @author Christian Bockermann
 * 
 */
public class JSONDatabase extends AbstractDatabase {

	static Logger log = LoggerFactory.getLogger(JSONDatabase.class);

	protected void populateDatabase(SourceURL url, Map<String, Data> database)
			throws Exception {
		JSONStream stream = new JSONStream(url);
		stream.init();
		readDatabase(stream, database);
		stream.close();
	}
}