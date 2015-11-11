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

import org.junit.Test;

import junit.framework.Assert;
import stream.Data;
import stream.ProcessContext;
import stream.expressions.ExpressionResolver;
import stream.runtime.ContainerContext;
import stream.runtime.ProcessContextImpl;

/**
 * 
 */

/**
 * @author chris
 * 
 */
public class ExpressionResolverTest {

	final ContainerContext ctx = new ContainerContext("container:test");
	final ProcessContext pc = new ProcessContextImpl("process:0", ctx);

	/**
	 * Test method for
	 * {@link stream.expressions.ExpressionResolver#extractName(java.lang.String)}
	 * .
	 */
	@Test
	public void testExtractName() {

		String var = "%{data.attribute}";
		String[] ref = ExpressionResolver.extractName(var);
		Assert.assertEquals(2, ref.length);
		Assert.assertEquals("data", ref[0]);
		Assert.assertEquals("attribute", ref[1]);

	}

	public void testExtractName2() {
		String var = "attribute";
		String[] ref = ExpressionResolver.extractName(var);
		Assert.assertEquals(2, ref.length);
		Assert.assertEquals("", ref[0]);
		Assert.assertEquals("data.attribute", ref[1]);
	}

	/**
	 * Test method for
	 * {@link stream.expressions.ExpressionResolver#resolve(java.lang.String, stream.runtime.Context, stream.Data)}
	 * .
	 */
	@Test
	public void testResolveContainer() {
		ctx.setProperty("test", "ABC");

		Data item = DataFactory.create();
		Object o = ExpressionResolver.resolve("%{container.test}", pc, item);
		Assert.assertEquals("ABC", o);
	}

	@Test
	public void testResolveProcess() {
		Long val = new Long(100L);
		pc.set("pclocal", val);
		Data item = DataFactory.create();
		Object o = ExpressionResolver.resolve("%{process.pclocal}", pc, item);
		Assert.assertEquals(val, o);
	}
}