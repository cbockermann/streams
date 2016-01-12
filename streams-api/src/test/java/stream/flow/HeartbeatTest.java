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

import junit.framework.Assert;

import org.junit.Test;

import stream.Data;
import stream.data.DataFactory;
import stream.data.ProcessContextMock;
import stream.io.Barrel;
import stream.io.Sink;
import stream.mock.SimpleMockBarrel;

public class HeartbeatTest {

	@Test
	public void test() throws Exception {
		// Config
		Heartbeat h = new Heartbeat();
		h.setKeys(new String[] { "@timestamp", "test" });
		h.setIndex("@timestamp");
		h.setEvery(1000);

		// Sink
		Barrel k = new SimpleMockBarrel();
		h.setSinks(new Sink[] { k });

		// INIT
		h.init(new ProcessContextMock());

		// Create Data

		for (long i = 0; i < 10010; i++) {
			Data d = DataFactory.create();
			d.put("@timestamp", i);
			d.put("test", "testvalue:" + i);
			h.process(d);
		}
		int c =0;
		boolean run = true;
		while (run) {
			Data d = k.read();
			if (d == null)
				run = false;
			else
				c++;
			
		}
		if(c!=10)
			Assert.fail();

	}
}
