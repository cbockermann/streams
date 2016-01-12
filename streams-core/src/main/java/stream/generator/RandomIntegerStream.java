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
package stream.generator;

import java.util.LinkedHashMap;
import java.util.Map;

import stream.Data;
import stream.annotations.Description;
import stream.annotations.Parameter;
import stream.data.DataFactory;
import stream.io.AbstractStream;
import stream.io.SourceURL;

/**
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 * 
 */
@Description(group = "Data Stream.Sources.Synthetic")
public class RandomIntegerStream extends AbstractStream {

	Map<String, Object> store = new LinkedHashMap<String, Object>();
	Map<String, Class<?>> attributes = new LinkedHashMap<String, Class<?>>();
	// Random[] random = null;
	SFMT19937[] random = null;
	String[] keys = new String[] { "att1" };

	public RandomIntegerStream() {
		super((SourceURL) null);
	}

	/**
	 * @return the keys
	 */
	public String[] getKeys() {
		return keys;
	}

	/**
	 * @param keys
	 *            the keys to set
	 */
	@Parameter(required = false, description = "The attribute names to create (comma separated)")
	public void setKeys(String[] keys) {
		this.keys = keys;
	}

	/**
	 * @see stream.io.AbstractStream#read()
	 */
	@Override
	public Data readNext() throws Exception {
		Data data = DataFactory.create();

		if (keys == null)
			keys = new String[] { "x1" };

		if (random == null)
			random = new SFMT19937[keys.length];

		for (int i = 0; i < keys.length; i++) {
			if (random[i] == null) {
				random[i] = new SFMT19937(i * 1000L);
			}

			data.put(keys[i], next(random[i]));
		}
		return data;
	}

	public Integer next(SFMT19937 rnd) {
		return rnd.next();
	}

	public Object get(String key) {
		return store.get(key);
	}

	public Object get(String key, Object init) {
		if (store.get(key) == null) {
			store.put(key, init);
			return init;
		}
		return store.get(key);

	}

	public void set(String key, Object val) {
		store.put(key, val);
	}

	/**
	 * @return the attributes
	 */
	public Map<String, Class<?>> getAttributes() {
		return attributes;
	}

}