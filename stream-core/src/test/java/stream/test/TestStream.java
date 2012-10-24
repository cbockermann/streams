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
package stream.test;

import java.io.Serializable;
import java.util.Random;

import org.junit.Test;

import stream.Data;
import stream.data.DataFactory;
import stream.io.AbstractDataStream;

/**
 * @author chris
 * 
 */
public class TestStream extends AbstractDataStream {

	Integer numberOfKeys = 10;
	Random rnd = new Random(2012L);
	Long id = 1L;
	String keys[];

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
	public void setKeys(String[] keys) {
		this.keys = keys;
	}

	/**
	 * @see stream.io.DataStream#close()
	 */
	@Override
	public void close() {
	}

	/**
	 * @see stream.io.AbstractDataStream#initReader()
	 */
	@Override
	protected void initReader() throws Exception {
	}

	/**
	 * @see stream.io.AbstractDataStream#readHeader()
	 */
	@Override
	public void readHeader() throws Exception {
	}

	/**
	 * @see stream.io.AbstractDataStream#readItem(stream.Data)
	 */
	@Override
	public Data readItem(Data instance) throws Exception {
		if (instance == null)
			instance = DataFactory.create();

		if (keys == null) {
			instance.put("@id", id);
			id++;

			for (int i = 0; i < numberOfKeys; i++) {
				String key = "x_" + i;
				Double value = rnd.nextDouble();
				instance.put(key, value);
			}
		} else {

			for (String key : keys) {
				Serializable value = null;
				if ("@id".equals(key)) {
					value = id++;
				} else {
					value = rnd.nextDouble();
				}

				instance.put(key, value);
			}
		}

		return instance;
	}

	@Test
	public void test() {

	}
}
