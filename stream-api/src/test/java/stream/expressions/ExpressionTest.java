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
