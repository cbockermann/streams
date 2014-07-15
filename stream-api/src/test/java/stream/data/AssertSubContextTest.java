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
package stream.data;

import junit.framework.Assert;

import org.junit.Test;

import stream.Data;

public class AssertSubContextTest {

	@Test
	public void test() {
		AssertSubContext actx = new AssertSubContext();

		ProcessContextMock ctx = new ProcessContextMock();

		Data data = DataFactory.create();

		ctx.set("test1", "test");
		ctx.set("test2", "test");
		ctx.set("test3", "test");
		ctx.set("test4", "test");

		data.put("test5", "test");
		data.put("test6", "test");
		data.put("test7", "test");
		data.put("test8", "test");

		try {
			actx.init(ctx);
			actx.process(data);

			actx.setKeys(new String[] { "test2", "test3" });
			actx.setContext("data");
			actx.init(ctx);

			Data result = actx.process(data);
			Assert.assertEquals(false, result.get("@subContext:complete"));

			actx.setKeys(new String[] { "test6", "test8" });
			actx.setContext("data");
			actx.init(ctx);

			result = actx.process(data);
			Assert.assertEquals(true, result.get("@subContext:complete"));

			actx.setKeys(new String[] { "test6", "test8" });
			actx.setContext("process");
			actx.init(ctx);

			result = actx.process(data);
			Assert.assertEquals(false, result.get("@subContext:complete"));

			actx.setKeys(new String[] { "test2", "test3" });
			actx.setContext("process");
			actx.init(ctx);

			result = actx.process(data);
			Assert.assertEquals(true, result.get("@subContext:complete"));

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
}
