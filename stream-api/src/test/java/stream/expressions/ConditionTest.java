/**
 * 
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
