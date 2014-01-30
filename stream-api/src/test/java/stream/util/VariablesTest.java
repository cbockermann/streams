package stream.util;

import junit.framework.Assert;

import org.junit.Test;

public class VariablesTest {

	@Test
	public void test() {
		Variables v = new Variables();
		//
		v.set("p1.v2_v5", "result");
		v.set("p3.v4", "v5");
		v.set("p2", "v2");
		v.set("p4", "v4");
		Assert.assertEquals("result", v.expand("${p1.${p2}_${p3.${p4}}}"));
		Assert.assertEquals("blah_result",
				v.expand("blah_${p1.${p2}_${p3.${p4}}}"));
		Assert.assertEquals("result_blah",
				v.expand("${p1.${p2}_${p3.${p4}}}_blah"));
		Assert.assertEquals("blah_result_blah",
				v.expand("blah_${p1.${p2}_${p3.${p4}}}_blah"));
	}
}
