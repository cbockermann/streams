package stream.data;

import junit.framework.Assert;

import org.junit.Test;

import stream.Data;
import stream.ProcessContext;

public class CopyValuesTest {

	@Test
	public void test() {
		CopyValues cv = new CopyValues();
		cv.setKeys(new String[] { "k1", "k2", "k3", "k4", "k5", "k6" });
		cv.setSourceCtx("data");
		cv.setTargetCtx("process");
		cv.setCondition("%{sourceCtx.key} != null");
		ProcessContext pc = new ProcessContextMock();
		try {

			cv.init(pc);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
		Data d = DataFactory.create();
		d.put("k1", 3d);
		cv.process(d);

		Assert.assertTrue(pc.get("k1") != null);
		Assert.assertTrue((Double) pc.get("k1") == 3d);
		Assert.assertTrue(pc.get("k2") == null);
		Assert.assertTrue(pc.get("k3") == null);
		Assert.assertTrue(pc.get("k4") == null);
		Assert.assertTrue(pc.get("k5") == null);
		Assert.assertTrue(pc.get("k6") == null);

		d.put("k5", 2d);
		cv.process(d);

		Assert.assertTrue(pc.get("k1") != null);
		Assert.assertTrue((Double) pc.get("k1") == 3d);
		Assert.assertTrue(pc.get("k2") == null);
		Assert.assertTrue(pc.get("k3") == null);
		Assert.assertTrue(pc.get("k4") == null);
		Assert.assertTrue(pc.get("k5") != null);
		Assert.assertTrue((Double) pc.get("k5") == 2d);
		Assert.assertTrue(pc.get("k6") == null);

	}
}
