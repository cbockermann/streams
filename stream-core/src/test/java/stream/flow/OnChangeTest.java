package stream.flow;

import junit.framework.Assert;

import org.junit.Test;

import stream.data.Data;
import stream.data.DataFactory;
import stream.data.SetValue;

public class OnChangeTest {

	@Test
	public void testFromEqNullToEqNull() {
		SetValue setValue = new SetValue();
		setValue.setKey("@result1");
		setValue.setValue("result1");
		SetValue setValue2 = new SetValue();
		setValue2.setKey("@result2");
		setValue2.setValue("result2");

		Data data = DataFactory.create();
		data.put("@test", null);
		data.put("@result1", null);
		data.put("@result2", null);

		OnChange onChange = new OnChange();
		onChange.setKey("%{data.@test}");
		onChange.getProcessors().add(setValue);

		// noChange
		onChange.process(data);
		Assert.assertEquals(null, data.get("@result1"));
		onChange.process(data);
		Assert.assertEquals(null, data.get("@result1"));

		// Change from null
		data.put("@test", "true");
		onChange.process(data);
		Assert.assertEquals("result1", data.get("@result1"));
		onChange.getProcessors().add(setValue2);
		onChange.process(data);
		Assert.assertEquals(null, data.get("@result2"));

		// Change from true
		data.put("@test", "false");
		data.put("@result1", null);
		onChange.process(data);
		Assert.assertEquals("result1", data.get("@result1"));
		data.put("@result2", null);
		onChange.process(data);
		Assert.assertEquals(null, data.get("@result2"));
	}

	@Test
	public void testFromEqNullToNeqNull() {

		SetValue setValue = new SetValue();
		setValue.setKey("@result1");
		setValue.setValue("result1");
		SetValue setValue2 = new SetValue();
		setValue2.setKey("@result2");
		setValue2.setValue("result2");

		Data data = DataFactory.create();
		data.put("@test", null);
		data.put("@result1", null);
		data.put("@result2", null);

		OnChange onChange = new OnChange();
		onChange.setKey("%{data.@test}");
		onChange.getProcessors().add(setValue);

		// noChange to = "null
		onChange.setTo("null");
		onChange.process(data);
		Assert.assertEquals(null, data.get("@result1"));

		// Change from "*" to = "null"
		data.put("@test", "test");
		onChange.process(data);
		Assert.assertEquals(null, data.get("@result1"));
		data.put("@test", null);
		onChange.process(data);
		Assert.assertEquals("result1", data.get("@result1"));

		// Change from test to "to"
		onChange.setTo("to");
		data.put("@test", "to");
		onChange.process(data);
		Assert.assertEquals("result1", data.get("@result1"));
		onChange.getProcessors().add(setValue2);
		onChange.process(data);
		Assert.assertEquals(null, data.get("@result2"));

	}

	@Test
	public void testFromNeqNullToeqNull() {

		SetValue setValue = new SetValue();
		setValue.setKey("@result1");
		setValue.setValue("result1");
		SetValue setValue2 = new SetValue();
		setValue2.setKey("@result2");
		setValue2.setValue("result2");

		Data data = DataFactory.create();
		data.put("@test", null);
		data.put("@result1", null);
		data.put("@result2", null);

		OnChange onChange = new OnChange();
		onChange.setKey("%{data.@test}");
		onChange.getProcessors().add(setValue);

		// noChange from = "null
		onChange.setFrom("null");
		onChange.process(data);
		Assert.assertEquals(null, data.get("@result1"));

		// Change from null to = "*"
		data.put("@test", "test");
		onChange.process(data);
		Assert.assertEquals("result1", data.get("@result1"));
		data.put("@test", "blah");
		onChange.getProcessors().add(setValue2);
		onChange.process(data);
		Assert.assertEquals(null, data.get("@result2"));

		// noChange from "*" to = "*'"
		onChange = new OnChange();
		onChange.setKey("%{data.@test}");
		onChange.getProcessors().add(setValue);
		onChange.setFrom("test");
		data.put("@test", "test");
		data.put("@result1", null);
		data.put("@result2", null);
		onChange.process(data);
		Assert.assertEquals(null, data.get("@result1"));
		data.put("@test", "test'");
		onChange.process(data);
		Assert.assertEquals("result1", data.get("@result1"));
		onChange.getProcessors().add(setValue2);
		onChange.process(data);
		Assert.assertEquals(null, data.get("@result2"));

	}

}
