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
package stream.logic;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.io.RandomStream;
import stream.logger.Message;

/**
 * @author chris
 * 
 */
public class MessageTest {

	static Logger log = LoggerFactory.getLogger(MessageTest.class);

	@Test
	public void test() throws Exception {

		RandomStream stream = new RandomStream(10000L);
		stream.getAttributes().put("x1", Double.class);
		stream.getAttributes().put("x2", Double.class);

		int i = 0;
		Message m = new Message();
		m.setMessage("%{x1} ist kleiner als 0.5 und größer als 0.1");
		m.setCondition("x1 @lt 0.5  and  x1 @ge 0.1");

		Data item = stream.readNext();
		while (item != null && i++ < 10) {
			m.process(item);
			item = stream.readNext();
		}

		// fail("Not yet implemented");
	}
}
