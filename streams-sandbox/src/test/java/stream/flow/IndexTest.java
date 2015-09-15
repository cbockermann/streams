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

import org.junit.Assert;
import org.junit.Test;

import stream.Data;
import stream.data.DataFactory;
import stream.data.ProcessContextMock;

public class IndexTest {

	@Test
	public void indexTest() throws Exception {
		Index index = new LongIndex();
		String indexId = "testIndex";
		index.setId(indexId);
		index.setIndexKey("time");

		index.init(new ProcessContextMock());

		Data data = DataFactory.create();
		long start = System.currentTimeMillis();
		long time = start;

		data.put("time", start);
		Data indexedData = index.process(data);
		Assert.assertEquals(0l, indexedData.get("@index:" + indexId));

		for (int i = 0; i < 1000; i++) {
			time += 1000;
			data.put("time", time);
			indexedData = index.process(data);

		}
		Assert.assertEquals(1000000l, indexedData.get("@index:" + indexId));

		// Reset
		index.reset();

		start = System.currentTimeMillis();
		time = start;
		data.put("time", start);
		indexedData = index.process(data);
		Assert.assertEquals(0l, indexedData.get("@index:" + indexId));

		for (int i = 0; i < 1000; i++) {
			time += 1000;
			data.put("time", time);
			indexedData = index.process(data);
		}
		Assert.assertEquals(1000000l, indexedData.get("@index:" + indexId));
		for (int i = 0; i < 1000; i++) {
			indexedData = index.process(data);
		}
		Assert.assertEquals(1000000l, indexedData.get("@index:" + indexId));
	}
}
