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
import stream.data.SetValue;

public class OnChangeTest {

	@Test
	public void testFromEqNullToEqNull() throws Exception {
		SetValue setValue = new SetValue();
		setValue.setKey("@result1");
		setValue.setValue("result1");
		setValue.init(new ProcessContextMock());
		SetValue setValue2 = new SetValue();
		setValue2.setKey("@result2");
		setValue2.setValue("result2");
		setValue2.init(new ProcessContextMock());
		
		Data data = DataFactory.create();
		data.put("@test", null);
		data.put("@result1", null);
		data.put("@result2", null);

		OnChange onChange = new OnChange();
		onChange.setKey("%{data.@test}");
		onChange.init(new ProcessContextMock());
		onChange.getProcessors().add(setValue);

		// noChange
		onChange.process(data);
		Assert.assertEquals(null, data.get("@result1"));
		onChange.process(data);
		Assert.assertEquals(null, data.get("@result1"));

		// Change from null
		data.put("@test", "true");
		onChange.process(data);
		Assert.assertEquals("result1", data.get("@result1"));
		onChange.getProcessors().add(setValue2);
		onChange.process(data);
		Assert.assertEquals(null, data.get("@result2"));

		// Change from true
		data.put("@test", "false");
		data.put("@result1", null);
		onChange.process(data);
		Assert.assertEquals("result1", data.get("@result1"));
		data.put("@result2", null);
		onChange.process(data);
		Assert.assertEquals(null, data.get("@result2"));
	}

	@Test
	public void testFromEqNullToNeqNull() throws Exception {

		SetValue setValue = new SetValue();
		setValue.setKey("@result1");
		setValue.setValue("result1");
		setValue.init(new ProcessContextMock());
		SetValue setValue2 = new SetValue();
		setValue2.setKey("@result2");
		setValue2.setValue("result2");
		setValue2.init(new ProcessContextMock());
		
		Data data = DataFactory.create();
		data.put("@test", null);
		data.put("@result1", null);
		data.put("@result2", null);

		
		
		OnChange onChange = new OnChange();
		onChange.setKey("%{data.@test}");
		// noChange to = "null
		onChange.setTo("null");
		onChange.init(new ProcessContextMock());
		
		onChange.getProcessors().add(setValue);

		onChange.process(data);
		Assert.assertEquals(null, data.get("@result1"));

		// Change from "*" to = "null"
		data.put("@test", "test");
		onChange.process(data);
		Assert.assertEquals(null, data.get("@result1"));
		
		data.put("@test", null);
		onChange.process(data);
		
		Assert.assertEquals("result1", data.get("@result1"));

		// Change from test to "to"
		onChange.setTo("to");
		data.put("@test", "to");
		onChange.process(data);
		Assert.assertEquals("result1", data.get("@result1"));
		onChange.getProcessors().add(setValue2);
		onChange.process(data);
		Assert.assertEquals(null, data.get("@result2"));

	}

	@Test
	public void testFromNeqNullToeqNull() throws Exception {

		SetValue setValue = new SetValue();
		setValue.setKey("@result1");
		setValue.setValue("result1");
		setValue.init(new ProcessContextMock());
		SetValue setValue2 = new SetValue();
		setValue2.setKey("@result2");
		setValue2.setValue("result2");
		setValue2.init(new ProcessContextMock());
		
		Data data = DataFactory.create();
		data.put("@test", null);
		data.put("@result1", null);
		data.put("@result2", null);

		OnChange onChange = new OnChange();
		onChange.setKey("%{data.@test}");
		onChange.init(new ProcessContextMock());
		onChange.getProcessors().add(setValue);

		// noChange from = "null
		onChange.setFrom("null");
		onChange.process(data);
		Assert.assertEquals(null, data.get("@result1"));

		// Change from null to = "*"
		data.put("@test", "test");
		onChange.process(data);
		Assert.assertEquals("result1", data.get("@result1"));
		data.put("@test", "blah");
		onChange.getProcessors().add(setValue2);
		onChange.process(data);
		Assert.assertEquals(null, data.get("@result2"));

		// noChange from "*" to = "*'"
		onChange = new OnChange();
		onChange.setKey("%{data.@test}");
		onChange.init(new ProcessContextMock());
		
		onChange.getProcessors().add(setValue);
		onChange.setFrom("test");
		data.put("@test", "test");
		data.put("@result1", null);
		data.put("@result2", null);
		onChange.process(data);
		Assert.assertEquals(null, data.get("@result1"));
		data.put("@test", "test'");
		onChange.process(data);
		
		Assert.assertEquals("result1", data.get("@result1"));
		onChange.getProcessors().add(setValue2);
		onChange.process(data);
		Assert.assertEquals(null, data.get("@result2"));

	}

}
