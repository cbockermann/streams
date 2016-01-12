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
package stream.flow;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.data.DataFactory;

/**
 * @author chris
 * 
 */
public class ConditionPerformanceTest {

	static Logger log = LoggerFactory.getLogger(ConditionPerformanceTest.class);

	@Test
	public void test() {

		Data item = DataFactory.create();
		item.put("x", 2.0d);
		item.put("t", 4.0d);
		item.put("y", "test");

		Skip skip = new Skip();
		skip.setCondition("%{data.x} < 4.0 and %{data.y} != null and %{data.x} > %{data.t}");

		Long start = System.currentTimeMillis();
		int rounds = 1000000;

		for (int i = 0; i < rounds; i++) {
			skip.process(item);
		}

		Long end = System.currentTimeMillis();
		log.info("Processed {} items in {} ms", rounds, (end - start));
		log.info("Rate is {}/second", rounds / ((end - start) / 1000.0d));

	}

}
