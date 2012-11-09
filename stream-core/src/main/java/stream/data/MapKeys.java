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
package stream.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.Processor;
import stream.annotations.Description;
import stream.annotations.Parameter;

/**
 * @author chris
 * 
 */
@Description(text = "", group = "Data Stream.Processing.Transformations.Attributes")
public class MapKeys implements Processor {

	static Logger log = LoggerFactory.getLogger(MapKeys.class);
	String oldKey;
	String newKey;
	String map;
	Map<String, String> mapping = new LinkedHashMap<String, String>();

	public MapKeys(String oldKey, String newKey) {
		this.oldKey = oldKey;
		this.newKey = newKey;
	}

	public MapKeys() {
		this.oldKey = "";
		this.newKey = "";
	}

	/**
	 * @return the oldKey
	 */
	public String getFrom() {
		return oldKey;
	}

	/**
	 * @param oldKey
	 *            the oldKey to set
	 */
	public void setFrom(String oldKey) {
		this.oldKey = oldKey;
	}

	/**
	 * @return the newKey
	 */
	public String getTo() {
		return newKey;
	}

	/**
	 * @param newKey
	 *            the newKey to set
	 */
	public void setTo(String newKey) {
		this.newKey = newKey;
	}

	/**
	 * @return the map
	 */
	public String getMap() {
		return map;
	}

	/**
	 * @param map
	 *            the map to set
	 */
	@Parameter(name = "map", required = false)
	public void setMap(String map) {
		try {
			if (map == null || map.trim().isEmpty()) {
				log.debug("No valid value '{}' for 'map' parameter.", map);
				return;
			}

			InputStream input = null;
			if (map.startsWith("classpath:")) {
				URL url = MapKeys.class.getResource(map.substring("classpath:"
						.length()));
				log.debug("Reading mapping from {}", url);
				input = url.openStream();
			} else {
				File file;
				if (map.startsWith("file:"))
					file = new File(map.substring("file:".length()));
				else
					file = new File(map);
				log.debug("Reading mappings from file {}", file);
				input = new FileInputStream(file);
			}

			Properties p = new Properties();
			p.load(input);

			for (Object key : p.keySet()) {
				log.debug("  adding mapping '{}' => '{}'", key,
						p.getProperty(key.toString()));
				mapping.put(key.toString(), p.getProperty(key.toString()));
			}

			this.map = map;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see stream.Processor#process(stream.Data)
	 */
	@Override
	public Data process(Data input) {
		if (oldKey != null && newKey != null && input.containsKey(oldKey)) {
			if (input.containsKey(newKey))
				log.warn("Overwriting existing key '{}'!", newKey);

			Serializable o = input.remove(oldKey);
			input.put(newKey, o);
		}

		for (String key : mapping.keySet()) {
			if (input.containsKey(key)) {
				Serializable value = input.remove(key);
				input.put(mapping.get(key), value);
			}
		}

		return input;
	}

	/**
	 * @return the mapping
	 */
	public Map<String, String> getMapping() {
		return mapping;
	}

	/**
	 * @param mapping
	 *            the mapping to set
	 */
	@Parameter(required = true, description = "A list of key mappings.")
	public void setMapping(Map<String, String> mapping) {
		this.mapping = mapping;
	}
}