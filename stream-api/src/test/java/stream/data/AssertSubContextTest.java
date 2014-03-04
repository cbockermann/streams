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
