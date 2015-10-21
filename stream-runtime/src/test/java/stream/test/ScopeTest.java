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

import static org.junit.Assert.fail;

import java.util.UUID;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.ProcessContext;
import stream.data.DataFactory;
import stream.data.PrintData;
import stream.data.SetValue;
import stream.flow.If;
import stream.runtime.ContainerContext;
import stream.runtime.ProcessContextImpl;

/**
 * @author chris
 * 
 */
public class ScopeTest {

	static Logger log = LoggerFactory.getLogger(ScopeTest.class);

	@Test
	public void test() {

		try {
			stream.runtime.DefaultProcess p = new stream.runtime.DefaultProcess();

			ContainerContext container = new ContainerContext(UUID.randomUUID().toString());
			ProcessContext ctx = new ProcessContextImpl("0", container);

			SetValue sv = new SetValue();
			sv.setKey("tetst");
			sv.setValue("1");
			sv.setScope(new String[] { "process" });

			p.add(sv);

			If cond = new If();
			cond.setCondition(
					"%{data.frame:red:avg} < 10 AND %{data.frame:green:avg} < 10 AND %{data.frame:blue:avg} < 10");

			SetValue sv2 = new SetValue();
			sv2.setKey("kapselStart");
			sv2.setValue("1");
			sv2.setScope(new String[] { "process" });

			cond.getProcessors().add(sv2);
			p.add(cond);

			PrintData pd = new PrintData();
			p.add(pd);

			p.init(container);

			Data item = DataFactory.create();
			item.put("frame:red:avg", 1.0d);
			item.put("frame:green:avg", 1.0d);
			item.put("frame:blue:avg", 1.0d);

			item = p.process(item);

			log.info("Value of 'kapselStart' is: {}", ctx.get("kapselStart"));
			log.info("Value of 'test' is: {}", ctx.get("test"));

		} catch (Exception e) {
			fail("Not yet implemented");
		}
	}
}
