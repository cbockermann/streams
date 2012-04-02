import junit.framework.Assert;

import org.junit.Test;

import stream.data.Data;
import stream.data.DataImpl;
import stream.runtime.ContainerContext;
import stream.runtime.ProcessContext;
import stream.runtime.ProcessContextImpl;
import stream.runtime.expressions.ExpressionResolver;

/**
 * 
 */

/**
 * @author chris
 * 
 */
public class ExpressionResolverTest {

	final ContainerContext ctx = new ContainerContext();
	final ProcessContext pc = new ProcessContextImpl(ctx);

	/**
	 * Test method for
	 * {@link stream.runtime.expressions.ExpressionResolver#extractName(java.lang.String)}
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
	 * {@link stream.runtime.expressions.ExpressionResolver#resolve(java.lang.String, stream.runtime.Context, stream.data.Data)}
	 * .
	 */
	@Test
	public void testResolveContainer() {
		ctx.setProperty("test", "ABC");

		Data item = new DataImpl();
		Object o = ExpressionResolver.resolve("%{container.test}", pc, item);
		Assert.assertEquals("ABC", o);
	}

	@Test
	public void testResolveProcess() {
		Long val = new Long(100L);
		pc.set("pclocal", val);
		Data item = new DataImpl();
		Object o = ExpressionResolver.resolve("%{process.pclocal}", pc, item);
		Assert.assertEquals(val, o);
	}
}