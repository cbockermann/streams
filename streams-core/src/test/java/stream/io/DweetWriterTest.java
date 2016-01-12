package stream.io;

import org.junit.Test;

import stream.Data;
import stream.data.DataFactory;
import stream.data.ProcessContextMock;

public class DweetWriterTest {

	@Test
	public void test() throws Exception {
		DweetWriter w = new DweetWriter();
		ProcessContextMock ctx = new ProcessContextMock();

		w.setMachine("DaMachine");
		w.setThing("DaThing");
		w.setId("TestID");
		w.setKeys(new String[] { "key1", "key2", "key3" });

		w.init(ctx);

		Data d = DataFactory.create();
		d.put("key1", 100d);
		d.put("key2", "State1");
		d.put("key3", 3l);
		w.process(d);

	}
}
