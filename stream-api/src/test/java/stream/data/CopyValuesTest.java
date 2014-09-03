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
