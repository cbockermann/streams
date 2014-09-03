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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import stream.Data;
import stream.ProcessContext;
import stream.data.DataFactory;
import stream.data.ProcessContextMock;

/**
 * @author chris
 * 
 */
public class ConditionTest {

	final ProcessContext ctx = new ProcessContextMock();
	Data item;

	@Before
	public void setup() {
		item = DataFactory.create();
		item.put("key", "1.0");
	}

	/**
	 * Test method for
	 * {@link stream.expressions.Condition#matches(stream.Context, stream.Data)}
	 * .
	 */
	@Test
	public void testEmptyConditionMatches() throws Exception {
		Condition c = new Condition("");
		Assert.assertTrue(c.matches(ctx, item));
	}

	@Test
	public void testNullConditionMatches() throws Exception {
		Condition c = new Condition(null);
		Assert.assertTrue(c.matches(ctx, item));
	}

}
