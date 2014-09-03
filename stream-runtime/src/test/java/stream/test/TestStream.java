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
package stream.test;

import java.util.Random;

import org.junit.Test;

import stream.Data;
import stream.data.DataFactory;
import stream.io.AbstractStream;

/**
 * @author chris
 * 
 */
public class TestStream extends AbstractStream {

	Integer numberOfKeys = 10;
	Random rnd = new Random(2012L);
	Long id = 0L;

	/**
	 * @see stream.io.AbstractStream#readItem(stream.Data)
	 */
	@Override
	public Data readNext() throws Exception {
		Data instance = DataFactory.create();

		instance.put("@id", id);
		id++;

		for (int i = 0; i < numberOfKeys; i++) {
			String key = "x[" + i + "]";
			Double value = rnd.nextDouble();
			instance.put(key, value);
		}

		return instance;
	}

	@Test
	public void test() {
		//
		// This method is only here to prevent junit tests from failing
		// as maven tries to run this class as test, but cannot find a
		// test-method without this method.
		//
	}

	@Override
	public String toString() {
		return "TestStream [numberOfKeys=" + numberOfKeys + ", rnd=" + rnd
				+ ", id=" + id + ", url=" + url + ", limit=" + limit
				+ ", count=" + count + "]";
	}

}
