/*
 *  streams library
 *
 *  Copyright (C) 2011-2012 by Christian Bockermann, Hendrik Blom
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
package stream.expressions;

import java.util.Arrays;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.ProcessContext;
import stream.Processor;
import stream.data.DataFactory;
import stream.data.ProcessContextMock;
import stream.data.StatisticsTest;
import stream.expressions.version2.ConditionFactory;
import stream.expressions.version2.StringArrayExpression;
import stream.flow.If;
import stream.flow.Skip;
import stream.util.MultiData;

/**
 * @author Hendrik Blom
 * 
 */
public class NewConditionsTest {

	static Logger log = LoggerFactory.getLogger(StatisticsTest.class);

	private final ProcessContext ctx = new ProcessContextMock();
	private Data item;
	private ConditionFactory cf;

	private int rounds = 10000;

	@Before
	public void setup() {
		item = DataFactory.create();
		item.put("key", "1.0");
		cf = new ConditionFactory();
	}

	@Test
	public void testEmptyCondition() throws Exception {
		Data data = DataFactory.create();
		// testTime("", data);
		assertCondition("", data, true);

	}

	@Test
	public void testNullCondition() throws Exception {
		Data data = DataFactory.create();
		data.put("b", 3d);
		stream.expressions.version2.Condition c = cf
				.create("%{data.b}>%{data.a}");
		Assert.assertNull(c.get(data));
		c = cf.create("%{data.a}>%{data.b}");
		Assert.assertNull(c.get(data));
		c = cf.create("%{data.a}>=%{data.b}");
		Assert.assertNull(c.get(data));
		c = cf.create("%{data.a}<%{data.b}");
		Assert.assertNull(c.get(data));
		c = cf.create("%{data.a}<=%{data.b}");
		Assert.assertNull(c.get(data));

		c = cf.create("%{data.a}=='svv'");
		Assert.assertNull(c.get(data));

	}

	@Test
	public void testSetCondition() throws Exception {
		//
		// Data data = DataFactory.create();
		// data.put("aa", 4d);
		// data.put("ab", 4d);
		// data.put("ac", 4d);
		// data.put("ba", 2d);
		//
		// assertCondition(
		// "%{data.aa}== 4d and %{data.{a.*}+} >3 and (%{data.ab}== 4d)",
		// data, true);
		// data.put("ad", 2d);
		// assertCondition(
		// "%{data.aa}== 4d and %{data.{a.*}+} >3and (%{data.ab}== 4d)",
		// data, false);

	}

	@Test
	public void testStringArrayExpression() throws Exception {
		StringArrayExpression e = new StringArrayExpression("blah1,blah2,blah3");

		String[] test = e.get(null, null);
		Assert.assertTrue(e.isStatic());
		long start = System.currentTimeMillis();
		for (int i = 0; i < rounds; i++) {

		}
		long stop = System.currentTimeMillis();
		System.out.println((stop - start) + ":" + Arrays.toString(test));
	}

	@Test
	public void testSourceURLExpression() throws Exception {

	}

	@Test
	public void testEqualCondition() throws Exception {

		MultiData md = new MultiData(rounds);
		testTime("%{data.test} == null", md);
		md.put("test", 3d);
		testTime("%{data.test} == 3d", md);
		testTime("%{data.test} == 3", md);
		testTime("%{data.test} == null", md);
		md.put("test", "test");
		testTime("'%{data.test}' == 'test'", md);

		// ******************************
		// Condition Double
		Data data = DataFactory.create();
		data.put("test", 3d);
		assertCondition("%{data.test} == 3d", data, true);
		assertCondition("3d == %{data.test}", data, true);
		data.put("test", 4d);
		assertCondition("%{data.test}==3d", data, false);
		assertCondition("3d == %{data.test}", data, false);
		// ******************************
		// Condition Integer
		data.put("test", 3);
		assertCondition("%{data.test} == 3", data, true);
		assertCondition("3 == %{data.test}", data, true);
		data.put("test", 4);
		assertCondition("%{data.test}==3", data, false);
		assertCondition("3 == %{data.test}", data, false);
		// ******************************
		// Condition String
		data.put("test", "test");
		assertCondition("%{data.test} == 'test'", data, true);
		assertCondition("'test' == %{data.test}", data, true);
		data.put("type", "sensorDisagreement");
		assertCondition("%{data.type} == 'test'", data, false);
		assertCondition("'test' == %{data.type}", data, false);
		assertCondition("%{data.type} == 'sensorDisagreement'", data, true);
		assertCondition("'sensorDisagreement' == %{data.type}", data, true);
		// ******************************
		// Condition Null
		data.put("test", 3);
		assertCondition("%{data.test} == null", data, false);
		assertCondition("null == %{data.test}", data, false);
		data.remove("test");
		assertCondition("%{data.test}==null", data, true);
		assertCondition("null == %{data.test}", data, true);
		data.put("test", "test");
		assertCondition("%{data.test} == null", data, false);
		assertCondition("null == %{data.test}", data, false);
		data.remove("test");
		assertCondition("%{data.test}==null", data, true);
		assertCondition("null == %{data.test}", data, true);

		data = DataFactory.create();
		assertCondition("%{data.test}!=null AND %{data.test}!='--'", data,
				false);
		data.put("test", "--");
		assertCondition("%{data.test}!=null AND %{data.test}!='--'", data,
				false);
		data.put("test", 3);
		assertCondition("%{data.test}!=null AND %{data.test}!='--'", data, true);

	}

	@Test
	public void testGreaterCondition() throws Exception {

		// ******************************
		// Condition Double
		Data data = DataFactory.create();
		data.put("test", 4d);
		assertCondition("%{data.test} > 3d", data, true);
		assertCondition("5d > %{data.test}", data, true);
		assertCondition("%{data.test} > 5d", data, false);
		assertCondition("3d > %{data.test}", data, false);
	}

	@Test
	public void testGreaterEqualsCondition() throws Exception {

		// ******************************
		// Condition Double
		Data data = DataFactory.create();
		data.put("test", 4d);
		assertCondition("%{data.test} >= 3d", data, true);
		assertCondition("5d >= %{data.test}", data, true);
		assertCondition("%{data.test} >= 5d", data, false);
		assertCondition("3d >= %{data.test}", data, false);
		assertCondition("%{data.test} >= 4d", data, true);
		assertCondition("4d >= %{data.test}", data, true);
	}

	@Test
	public void testLesserCondition() throws Exception {

		// ******************************
		// Condition Double
		Data data = DataFactory.create();
		data.put("test", 4d);
		assertCondition("%{data.test} < 3d", data, false);
		assertCondition("5d < %{data.test}", data, false);
		assertCondition("%{data.test} < 5d", data, true);
		assertCondition("3d < %{data.test}", data, true);
	}

	@Test
	public void testLesserEqualsCondition() throws Exception {

		// ******************************
		// Condition Double
		Data data = DataFactory.create();
		data.put("test", 4d);
		assertCondition("%{data.test} <= 3d", data, false);
		assertCondition("5d <= %{data.test}", data, false);
		assertCondition("%{data.test} <= 5d", data, true);
		assertCondition("5d >= %{data.test}", data, true);
		assertCondition("%{data.test} <= 4d", data, true);
		assertCondition("4d <= %{data.test}", data, true);
	}

	@Test
	public void testNotEqualCondition() throws Exception {

		MultiData md = new MultiData(rounds);
		testTime("%{data.test} != null", md);
		md.put("test", 3d);
		testTime("%{data.test} != 3d", md);
		testTime("%{data.test} != 3", md);
		testTime("%{data.test} != null", md);

		// Assert
		// Condition Double
		Data data = DataFactory.create();
		data.put("test", 3d);
		assertCondition("%{data.test} != 3d", data, false);
		assertCondition("3d != %{data.test}", data, false);
		data.put("test", 4d);
		assertCondition("%{data.test}!=3d", data, true);
		assertCondition("3d != %{data.test}", data, true);
		// ******************************
		// Condition Integer
		data.put("test", 3);
		assertCondition("%{data.test} != 3", data, false);
		assertCondition("3!= %{data.test}", data, false);
		data.put("test", 4);
		assertCondition("%{data.test}!=3", data, true);
		assertCondition("3 != %{data.test}", data, true);
		// ******************************
		// Condition Null
		data.put("test", 3);
		assertCondition("%{data.test} != null", data, true);
		assertCondition("null != %{data.test}", data, true);
		data.remove("test");
		assertCondition("%{data.test}!=null", data, false);
		assertCondition("null != %{data.test}", data, false);
		// Condition String
		data.put("test", "test");
		assertCondition("%{data.test} != 'test'", data, false);
		assertCondition("'test' != %{data.test}", data, false);

	}

	@Test
	public void testAndCondition() throws Exception {

		MultiData md = new MultiData(rounds);
		md.put("test2", 4d);
		md.put("test", 3d);
		testTime("%{data.test} == 3d and %{data.test} != 4d", md);
		testTime("(%{data.test} == 3d) and (%{data.test} != 4d)", md);
		testTime("%{data.test} == 3d and %{data.test2} != 4d", md);
		testTime("(%{data.test} == 3d) and (%{data.test2} == 4d)", md);
		testTime("(%{data.test} == 3d) and (%{data.test2} != null)", md);
		testTime("(%{data.test} == 3d) and %{data.test2} != null", md);
		testTime(
				"((%{data.test} == 3d) and (%{data.test2} != null)) and  %{data.test3} == null",
				md);

		// Assert
		// Condition Double
		Data data = DataFactory.create();
		data.put("test2", 4d);
		data.put("test", 3d);
		assertCondition("%{data.test} == 3d and %{data.test} != 4d", data, true);
		assertCondition("(%{data.test} == 3d) and (%{data.test} != 4d)", data,
				true);
		assertCondition("%{data.test} == 3d and (%{data.test} != 4d)", data,
				true);
		assertCondition("%{data.test} == 3d and %{data.test2} != 4d", data,
				false);
		assertCondition("(%{data.test} == 3d) and (%{data.test2} == 4d)", data,
				true);
		assertCondition("(%{data.test} == 3d) and (%{data.test2} != null)",
				data, true);
		assertCondition(
				"(%{data.test} == 3d) and (%{data.test2} != null)and (%{data.test2} != 1d) and(%{data.test2} != 5)",
				data, true);
		assertCondition(
				"%{data.test} == 3d and %{data.test2} != nulland %{data.test2} != 1d and%{data.test2} != 5",
				data, true);
		assertCondition(
				"%{data.test} == 3d and %{data.test2} != nullAND (%{data.test2} != 1d) and%{data.test2} != 5",
				data, true);
		assertCondition(
				"((%{data.test} == 3d) and (%{data.test2} != null)) AND  %{data.test3} == null",
				data, true);
		assertCondition(
				"(((%{data.test} == 3d) and (%{data.test2} != null)) AND  %{data.test3} == null)",
				data, true);

		// **************
		data = DataFactory.create();
		data.put("test", "--");

		assertCondition("%{data.test} != '--' AND %{data.test} != null", data,
				false);
		data.put("test", null);
		assertCondition("%{data.test} != '--' AND %{data.test} != null", data,
				false);
		data.put("test", 123);
		assertCondition("%{data.test} != '--' AND %{data.test} != null", data,
				true);

	}

	@Test
	public void testOrCondition() throws Exception {

		MultiData md = new MultiData(rounds);
		md.put("test2", 4d);
		md.put("test", 3d);
		testTime("%{data.test} == 3d or %{data.test} != 4d", md);
		testTime("(%{data.test} == 3d) or (%{data.test} != 4d)", md);
		testTime("%{data.test} == 3d or %{data.test2} != 4d", md);
		testTime("(%{data.test} == 3d) or (%{data.test2} == 4d)", md);
		testTime("(%{data.test} == 3d) or (%{data.test2} != null)", md);
		testTime("(%{data.test} == 3d) or %{data.test2} != null", md);

		// Assert
		// Condition Double
		Data data = DataFactory.create();
		data.put("test2", 4d);
		data.put("test", 3d);
		assertCondition("%{data.test} == 3d or %{data.test} != 4d", data, true);
		assertCondition("(%{data.test} == 3d) or (%{data.test} != 4d)", data,
				true);
		assertCondition("%{data.test} == 3d or (%{data.test} != 4d)", data,
				true);
		assertCondition("%{data.test} == 3d or %{data.test2} != 4d", data, true);
		assertCondition("(%{data.test} == 3d) or (%{data.test2} == 4d)", data,
				true);
		assertCondition("(%{data.test} == 3d) or (%{data.test2} != null)",
				data, true);

		assertCondition("%{data.test} == 3d OR %{data.test} != 4d", data, true);
		assertCondition("(%{data.test} == 3d) OR (%{data.test} != 4d)", data,
				true);
		assertCondition("%{data.test} == 3d OR (%{data.test} != 4d)", data,
				true);
		assertCondition("%{data.test} == 3d OR %{data.test2} != 4d", data, true);
		assertCondition("(%{data.test} == 3d) OR (%{data.test2} == 4d)", data,
				true);
		assertCondition("(%{data.test} == 3d) OR (%{data.test2} != null)",
				data, true);

	}

	@Test
	public void testAndOrCondition() throws Exception {

		MultiData md = new MultiData(rounds);
		md.put("test2", 4d);
		md.put("test", 3d);
		testTime(
				"(((%{data.test} == 3d) and (%{data.test2} != null)) or  %{data.test3} != null)",
				md);

		Data data = DataFactory.create();
		data.put("test2", 4d);
		data.put("test", 3d);
		assertCondition(
				"(((%{data.test} == 3d) and (%{data.test2} != null)) or  %{data.test3} != null)",
				data, true);
		assertCondition(
				"(((%{data.test} == 3d) and (%{data.test2} != null)) or  %{data.test3} == null)",
				data, true);
		assertCondition(
				"(((%{data.test} == 2d) and (%{data.test2} != null)) or  %{data.test3} != null)",
				data, false);
	}

	@Test
	public void testEqualConditionIf() throws Exception {

		String condition = "%{data.test} == 3d";
		If ifP = new If();
		ifP.setCondition(condition);
		ifP.init(new ProcessContextMock());

		MultiData md = new MultiData(rounds);
		md.put("test", 3d);
		testTime(ifP, md);
	}

	@Test
	public void testNotEqualConditionSkip() throws Exception {
		String condition = "%{data.test} == 3d";
		Skip skip = new Skip();
		skip.setCondition(condition);
		skip.init(new ProcessContextMock());

		MultiData md = new MultiData(rounds);
		md.put("test", 3d);
		testTime(skip, md);
	}

	@Test
	public void testEmptyConditionNewSkip() throws Exception {
		Skip skip = new Skip();
		skip.setCondition("");
		skip.init(new ProcessContextMock());
		MultiData md = new MultiData(rounds);
		testTime(skip, md);
	}

	@Test
	public void testRXCondition() throws Exception {

		// ******************************
		Data data = DataFactory.create();
		data.put("name", "*");
		assertCondition("%{data.name} @rx '.*'", data, true);
		assertCondition("%{data.name} @nrx '.*'", data, false);

		data.put("name", "network roaming");
		assertCondition("%{data.name} @nrx 'network.*'", data, false);
		assertCondition("%{data.name} @rx 'network.*'", data, true);

		data.put("name", "47");
		assertCondition("%{data.name} @nrx '[1-9][0-9]'", data, false);
		assertCondition("%{data.name} @rx '[1-9][0-9]'", data, true);

		data.put("name", "199");
		assertCondition("%{data.name} @nrx '[1-9][0-9]'", data, true);
		assertCondition("%{data.name} @rx '[1-9][0-9]'", data, false);
		data.put("mail", "horst19@ober-horst.de");
		assertCondition(
				"%{data.mail} @rx '[A-za-z0-9]+@[a-zA-Z0-9.-]+\\.[A-Za-z]+'",
				data, true);
		assertCondition(
				"%{data.mail} @nrx '[A-za-z0-9]+@[a-zA-Z0-9.-]+\\.[A-Za-z]+'",
				data, false);

	}

	private void assertCondition(String condition, Data data, Boolean result)
			throws Exception {

		stream.expressions.version2.Condition c = cf.create(condition);

		Assert.assertEquals(c.get(ctx, data), result);
	}

	@SuppressWarnings("unused")
	private void testTime(String condition, Data data) throws Exception {
		Boolean b = null;
		stream.expressions.version2.Condition c = cf.create(condition);
		long start = System.currentTimeMillis();
		for (int i = 0; i < rounds; i++) {
			b = c.get(ctx, data);
		}
		long end = System.currentTimeMillis();
		long time = end - start == 0 ? 1 : end - start;
		log.info("Condition: {}", c);
		log.info("{} elements nedded {} ms: {} mio items/s", rounds, time,
				((rounds / time) / 1000));
		log.info("{} ", data.toString());
	}

	@SuppressWarnings("unused")
	private void testTime(String condition, stream.util.MultiData data)
			throws Exception {
		Boolean b = null;
		stream.expressions.version2.Condition c = cf.create(condition);
		long start = System.currentTimeMillis();
		for (Data d : data.get()) {
			b = c.get(ctx, d);
		}
		long end = System.currentTimeMillis();

		int size = data.size();
		log.info("Condition: {}", c);
		long time = end - start == 0 ? 1 : end - start;
		log.info("{} elements nedded {} ms: {} mio items/s", size, time,
				(size / time) / 1000);
		log.info("{} ", data.toString());
	}

	@SuppressWarnings("unused")
	private void testTime(Processor p, Data data) throws Exception {
		Data d = null;
		long start = System.currentTimeMillis();
		for (int i = 0; i < rounds; i++) {
			d = p.process(data);
		}
		long end = System.currentTimeMillis();

		log.info("Processor: {}", p);
		long time = end - start == 0 ? 1 : end - start;
		log.info("{} elements nedded {} ms: {} mio items/s", rounds, time,
				((rounds / time) / 1000));
		log.info("{} ", data.toString());
	}

	@SuppressWarnings("unused")
	private void testTime(Processor p, stream.util.MultiData data)
			throws Exception {
		Data da = null;
		long start = System.currentTimeMillis();
		for (Data d : data.get()) {
			da = p.process(d);
		}
		long end = System.currentTimeMillis();

		int size = data.size();
		log.info("Processor: {}", p);
		long time = end - start == 0 ? 1 : end - start;
		log.info("{} elements nedded {} ms: {} mio items/s", size, time,
				((size / time) / 1000));
		log.info("{} ", data.toString());
	}

}
