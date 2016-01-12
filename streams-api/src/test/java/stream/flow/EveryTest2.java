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
import stream.ProcessContext;
import stream.data.DataFactory;
import stream.data.ProcessContextMock;
import stream.data.SetValue;

public class EveryTest2 {

	@Test
	public void test() throws Exception {
		Every e = new Every();
		ProcessContext c = new ProcessContextMock();
		e.setN(100l);
		
		
		SetValue sv = new SetValue();
		sv.setScope(new String[] {"data"});
		sv.setKey("test");
		sv.setValue("test");
		e.add(sv);
		e.init(c);
		
		int count = 0;
		
		for(int i=0; i<1000;i++){
			Data d = DataFactory.create();
			d = e.process(d);
			if(d.containsKey("test"))
				count++;
		}
		Assert.assertEquals(10, count);
		
		
	}
	

}
