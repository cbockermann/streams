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
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import stream.Processor;
import stream.annotations.Description;
import stream.data.Data;

@Description(text = "", group = "Data Stream.Processing.Transformations.Data")
public class MapValues implements Processor {

	String key = "@label";
	String map;

	Serializable defaultValue = new Double(-1.0d);
	Map<Serializable, Serializable> mapping = new HashMap<Serializable, Serializable>();

	String from = null;
	String to = null;

	public void addMapping(Serializable from, Serializable to) {
		mapping.put(from, to);
	}

	public void setDefault(Serializable defaultValue) {
		this.defaultValue = defaultValue;
	}

	public Serializable getDefault() {
		return this.defaultValue;
	}

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
	public void setKey(String key) {
		this.key = key;
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
	public void setMap(String map) {
		try {
			File file = new File(map);
			Properties p = new Properties();
			p.load(new FileInputStream(file));

			for (Object key : p.keySet()) {
				mapping.put(key.toString(), p.getProperty(key.toString()));
			}

			this.map = map;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return the from
	 */
	public String getFrom() {
		return from;
	}

	/**
	 * @param from
	 *            the from to set
	 */
	public void setFrom(String from) {
		this.from = from;
	}

	/**
	 * @return the to
	 */
	public String getTo() {
		return to;
	}

	/**
	 * @param to
	 *            the to to set
	 */
	public void setTo(String to) {
		this.to = to;
	}

	@Override
	public Data process(Data data) {
		Serializable val = data.get(key);
		if (val == null)
			return data;

		if (from != null && to != null) {
			if (from.equals(val.toString())) {
				data.put(key, to);
			}
		}

		Serializable to = mapping.get(val);
		if (to == null) {
			data.put(key, to);
		} else {
			data.put(key, defaultValue);
		}

		return data;
	}
}