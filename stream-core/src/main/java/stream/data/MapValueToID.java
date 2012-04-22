/*
 *  stream.ai
 *
 *  Copyright (C) 2011-2012 by Christian Bockermann, Hendrik Blom
 * 
 *  stream.ai is a library, API and runtime environment for processing high
 *  volume data streams. It is composed of three submodules "stream-api",
 *  "stream-core" and "stream-runtime".
 *
 *  The stream.ai library (and its submodules) is free software: you can 
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
package stream.data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Processor;
import stream.annotations.Description;
import stream.annotations.Parameter;
import stream.data.Data;

/**
 * @author chris
 * 
 */
@Description(group = "Data Stream.Processing.Transformations.Data", text = "")
public class MapValueToID implements Processor {

	static Logger log = LoggerFactory.getLogger(MapValueToID.class);

	Integer maxId = 0;

	String key = "key";

	Map<String, Integer> mapping = new HashMap<String, Integer>();

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key
	 *            the key to set
	 */
	@Parameter
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @see stream.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data data) {

		if (key == null) {
			log.error("No key specified!");
			return data;
		}

		Serializable val = data.get(key);
		if (val == null) {
			log.debug("No value found in data-item! Skipping that item.");
			return data;
		}

		Integer id = mapping.get(val.toString());
		if (id == null) {
			id = 1 + maxId;
			maxId++;
			log.debug("Adding new ID {} for value {}", id, val);
			mapping.put(val.toString(), id);
		} else {
			log.debug("Found existing ID mapping {} => {}", val, id);
		}

		mapping.put(key, id);
		data.put(key, id);
		return data;
	}
}