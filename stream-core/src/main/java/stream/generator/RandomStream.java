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
package stream.generator;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import stream.data.Data;
import stream.data.DataFactory;

/**
 * @author chris
 * 
 */
public class RandomStream extends GeneratorDataStream {

	Random random;
	Map<String, Class<?>> attributes = new LinkedHashMap<String, Class<?>>();
	Map<String, Object> store = new LinkedHashMap<String, Object>();

	public RandomStream() {
		random = new Random();
		attributes.put("att1", Double.class);
	}

	/**
	 * @see stream.io.DataStream#getAttributes()
	 */
	@Override
	public Map<String, Class<?>> getAttributes() {
		return attributes;
	}

	/**
	 * @see stream.io.AbstractDataStream#readNext()
	 */
	@Override
	public Data readNext() throws Exception {
		Data map = DataFactory.create();
		map.put("att1", next(random));
		return map;
	}

	public Data readNext(Data data) throws Exception {
		if (data == null)
			return readNext();

		data.clear();
		data.put("att1", next(random));
		return data;
	}

	public Double next(Random rnd) {
		return 0.1 * (5.0 + rnd.nextGaussian());
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

	public void close() {
	}
}