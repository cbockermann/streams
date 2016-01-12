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

import stream.ProcessorList;
import stream.data.DataFactory;
import stream.mock.SimpleMockProcessor;

public class OnFinishProcessTest {

	@Test
	public void test() {
		stream.runtime.DefaultProcess p = new stream.runtime.DefaultProcess();
		ProcessorList onFinish = new OnFinish();
		SimpleMockProcessor m1 = new SimpleMockProcessor();
		SimpleMockProcessor m2 = new SimpleMockProcessor();
		onFinish.getProcessors().add(m2);

		p.add(m1);
		p.add(onFinish);
		// Process
		p.process(DataFactory.create());
		Assert.assertTrue(m1.getProcessed());
		Assert.assertFalse(m2.getProcessed());
		// Finish
		try {
			p.finish();
			Assert.assertTrue(m1.getFinished());
			Assert.assertTrue(m2.getProcessed());
			Assert.assertTrue(m2.getFinished());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
