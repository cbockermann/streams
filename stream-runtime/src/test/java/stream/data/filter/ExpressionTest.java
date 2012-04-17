/**
 * 
 */
package stream.data.filter;

import static org.junit.Assert.fail;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import stream.data.Data;
import stream.data.DataImpl;
import stream.expressions.Expression;
import stream.expressions.ExpressionCompiler;
import stream.runtime.LocalContext;

/**
 * @author chris
 * 
 */
public class ExpressionTest {

	Data item = new DataImpl();

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