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
package stream.io;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.DataFactory;

/**
 * 
 * 
 * @author chris
 * @deprecated
 */
public class DataStreamLoopTest {

	static Logger log = LoggerFactory.getLogger(DataStreamLoopTest.class);

	@Test
	public void testReplication() throws Exception {

		List<Data> items = new ArrayList<Data>();
		for (int i = 0; i < 10; i++) {
			Data item = DataFactory.create();
			item.put("@id", i + "");
			item.put("x", Math.random());
			items.add(item);
		}

		DataStreamLoop loop = new DataStreamLoop();
		loop.setSource(new ListDataStream(items));
		loop.setRepeat(2);
		loop.setShuffle(true);

		List<Data> result = new ArrayList<Data>();
		Data datum = loop.readNext();
		while (datum != null) {
			result.add(datum);
			log.info("{}", datum);
			datum = loop.readNext();
		}

		log.info("loop resulted in {} items", result.size());
		org.junit.Assert.assertTrue(30 == result.size());
	}

}
