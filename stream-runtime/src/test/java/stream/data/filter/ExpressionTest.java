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
package stream.data.filter;

import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;
import stream.Data;
import stream.data.DataFactory;
import stream.expressions.Expression;
import stream.expressions.ExpressionCompiler;
import stream.runtime.LocalContext;

/**
 * @author chris
 * 
 */
public class ExpressionTest {

	Data item = DataFactory.create();

	@Before
	public void init() {
		item.clear();
		item.put("xa", "A");
		item.put("x2.0", 2.0);
		item.put("x2.1", 2.1);
	}

	@Test
	public void parseTest() {

		try {
			ExpressionCompiler.parse("TriggerType == 4 ");
		} catch (Exception e) {
			fail("Parsing failed: " + e.getMessage());
		}
	}

	protected boolean eval(Data item, String expression) throws Exception {
		Expression e = ExpressionCompiler.parse(expression);
		return e.matches(new LocalContext(), item);
	}

	@Test
	public void testGt() throws Exception {
		Assert.assertTrue(eval(item, "%{data.x2.0} @gt 1.9"));
		Assert.assertTrue(eval(item, "%{data.x2.0} > 1.9"));
		Assert.assertFalse(eval(item, "%{data.x2.0} > 2.0"));
		Assert.assertFalse(eval(item, "%{data.x2.0} > %{data.x2.1}"));
		Assert.assertTrue(eval(item, "%{data.x2.1} > %{data.x2.0}"));
	}

	@Test
	public void testGe() throws Exception {
		Assert.assertTrue(eval(item, "%{data.x2.0} @ge 1.9"));
		Assert.assertTrue(eval(item, "%{data.x2.0} @ge 2.0"));
		Assert.assertFalse(eval(item, "%{data.x2.0} @ge 2.1"));
		Assert.assertTrue(eval(item, "%{data.x2.0} >= 2.0"));
		Assert.assertTrue(eval(item, "%{data.x2.0} >= %{data.x2.0}"));
		Assert.assertFalse(eval(item, "%{data.x2.0} >= 2.1"));
		Assert.assertFalse(eval(item, "%{data.x2.0} >= %{data.x2.1}"));
	}

	@Test
	public void testLt() throws Exception {
		item.put("x", 2.0d);
		Assert.assertTrue(eval(item, "%{data.x} @lt 2.1"));
		Assert.assertTrue(eval(item, "%{data.x} < 2.1"));
		Assert.assertFalse(eval(item, "%{data.x} < 2.0"));
		Assert.assertTrue(eval(item, "%{data.x} < %{data.x2.1}"));
		Assert.assertFalse(eval(item, "%{data.x} < %{data.x2.0}"));
	}

	@Test
	public void testLe() throws Exception {
		item.put("x", 2.1d);
		Assert.assertTrue(eval(item, "%{data.x} @le 2.1"));
		Assert.assertTrue(eval(item, "%{data.x} @le 2.2"));
		Assert.assertFalse(eval(item, "%{data.x} @le 2.0"));
		Assert.assertTrue(eval(item, "%{data.x} <= 2.1"));
		Assert.assertFalse(eval(item, "%{data.x} <= 2.0"));
		Assert.assertTrue(eval(item, "%{data.x} <= %{data.x2.1}"));
		Assert.assertFalse(eval(item, "%{data.x} <= %{data.x2.0}"));
	}

	@Test
	public void testEq() throws Exception {
		item.put("x", 2.1d);
		item.put("xa", "A");
		Assert.assertTrue(eval(item, "%{data.x} @eq 2.1"));
		Assert.assertFalse(eval(item, "x @eq 2.1"));
		Assert.assertTrue(eval(item, "%{data.x} = 2.1"));
		Assert.assertTrue(eval(item, "%{data.x} == 2.1"));
		Assert.assertFalse(eval(item, "%{data.x} == 2.0"));
		Assert.assertTrue(eval(item, "%{data.x} == %{data.x2.1}"));
		Assert.assertFalse(eval(item, "%{data.x} == %{data.x2.0}"));

		Assert.assertFalse(eval(item, "%{data.x} == A"));
		Assert.assertTrue(eval(item, "%{data.xa} == A"));
	}

	@Test
	public void testNeq() throws Exception {
		item.put("x", 2.1d);
		item.put("xa", "A");
		Assert.assertFalse(eval(item, "%{data.x} @neq 2.1"));
		Assert.assertFalse(eval(item, "%{data.x} != 2.1"));

		Assert.assertTrue(eval(item, "%{data.x} != 2.0"));

		Assert.assertFalse(eval(item, "%{data.x} != %{data.x2.1}"));
		Assert.assertTrue(eval(item, "%{data.x} != %{data.x2.0}"));

		Assert.assertTrue(eval(item, "%{data.x} != A"));
		Assert.assertFalse(eval(item, "%{data.xa} != A"));
	}

}