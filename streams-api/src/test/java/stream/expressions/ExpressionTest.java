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
package stream.expressions;

import junit.framework.Assert;

import org.junit.Test;

import stream.Context;
import stream.Data;
import stream.data.DataFactory;
import stream.data.ProcessContextMock;
import stream.expressions.version2.Expression;
import stream.expressions.version2.StringExpression;

public class ExpressionTest {

	@Test
	public void testStringExpressions() throws Exception {

		Data data = DataFactory.create();
		Context context = new ProcessContextMock();
		data.put("test", "test");
		String s1 = "fsamdksfdkösdkf";
		String s2 = "friwueironv";
		Expression<String> s = new StringExpression(s1 + "%{data.test}" + s2);
		Assert.assertEquals(s.get(context, data), s1 + "test" + s2);
		s = new StringExpression(s1 + "%{data.test}");
		Assert.assertEquals(s.get(context, data), s1 + "test");

		s = new StringExpression("%{data.test}" + s2);
		Assert.assertEquals(s.get(context, data), "test" + s2);
	}
}
