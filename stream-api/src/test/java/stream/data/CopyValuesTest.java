package stream.data;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import stream.Data;
import stream.ProcessContext;
import stream.util.MultiData;

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

	@Test
	public void test2() {
		CopyValues cv = new CopyValues();
		cv.setKeys(new String[] { "k1", "k2", "k3", "k4", "k5", "k6" });
		cv.setSourceCtx("process");
		cv.setTargetCtx("data");
		cv.setCondition("%{sourceCtx.key} != null");
		ProcessContext pc = new ProcessContextMock();
		try {

			cv.init(pc);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
		Data d = DataFactory.create();
		pc.set("k1", 3d);
		cv.process(d);

		Assert.assertTrue(d.get("k1") != null);
		Assert.assertTrue((Double) d.get("k1") == 3d);
		Assert.assertTrue(d.get("k2") == null);
		Assert.assertTrue(d.get("k3") == null);
		Assert.assertTrue(d.get("k4") == null);
		Assert.assertTrue(d.get("k5") == null);
		Assert.assertTrue(d.get("k6") == null);

		pc.set("k4", 4d);
		pc.set("k5", 2d);
		cv.process(d);

		Assert.assertTrue(d.get("k1") != null);
		Assert.assertTrue((Double) d.get("k1") == 3d);
		Assert.assertTrue(d.get("k2") == null);
		Assert.assertTrue(d.get("k3") == null);
		Assert.assertTrue(d.get("k4") != null);
		Assert.assertTrue((Double) d.get("k4") == 4d);
		Assert.assertTrue(d.get("k5") != null);
		Assert.assertTrue((Double) d.get("k5") == 2d);
		Assert.assertTrue(d.get("k6") == null);

	}
	
	@Test
	public void testTime() {

		int k = 100;
		int n = 100000;

		CopyValues cv = new CopyValues();
		cv.setKeys(new String[] { "k1", "k2", "k3", "k4", "k5", "k6" });
		cv.setCondition("%{sourceCtx.key} != null");
		
		List<String> keys = new ArrayList<String>();

		for (int i = 0; i < k; i++) {
			keys.add("k" + i);
		}
		cv.setKeys(keys.toArray(new String[keys.size()]));

		cv.setSourceCtx("process");
		cv.setTargetCtx("data");

		ProcessContext pc = new ProcessContextMock();

		try {

			cv.init(pc);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}

		MultiData md = new MultiData(n);
		pc.set("k1", 3d);

		long start = System.currentTimeMillis();
		for (Data d : md.get()) {
			cv.process(d);
		}
		long stop = System.currentTimeMillis();
		System.out.println(stop - start);

	}
}
